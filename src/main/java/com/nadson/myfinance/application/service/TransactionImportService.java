package com.nadson.myfinance.application.service;

import com.nadson.myfinance.application.port.in.CreateTransactionPort;
import com.nadson.myfinance.application.port.in.TransferPort;
import com.nadson.myfinance.application.port.in.ListAccountsByUserPort;
import com.nadson.myfinance.application.port.out.CategoryRepositoryPort;
import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.entity.Category;
import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.domain.enums.TransactionType;
import com.nadson.myfinance.domain.service.CategorizationEngine;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.NumberFormat;
import java.util.Locale;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionImportService {

    private final TransferPort transferPort;
    private final CreateTransactionPort createTransactionPort;
    private final TransactionRepositoryPort transactionRepositoryPort;
    private final ListAccountsByUserPort listAccountsByUserPort;
    private final CategoryRepositoryPort categoryRepositoryPort;

    public TransactionImportService(TransferPort transferPort,
                                    CreateTransactionPort createTransactionPort,
                                    TransactionRepositoryPort transactionRepositoryPort,
                                    ListAccountsByUserPort listAccountsByUserPort, CategoryRepositoryPort categoryRepositoryPort) {
        this.transferPort = transferPort;
        this.createTransactionPort = createTransactionPort;
        this.transactionRepositoryPort = transactionRepositoryPort;
        this.listAccountsByUserPort = listAccountsByUserPort;
        this.categoryRepositoryPort = categoryRepositoryPort;
    }

    public void importCsv(MultipartFile file, String bankType, UUID userId) throws Exception {
        List<Account> userAccounts = listAccountsByUserPort.execute(userId);

        UUID interId = findAccountIdByName(userAccounts, "Inter");
        UUID mpId = findAccountIdByName(userAccounts, "Mercado Pago");
        UUID investmentId = findAccountIdByName(userAccounts, "Investimento");

        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setDelimiter(';')
                .setIgnoreEmptyLines(true)
                .build();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVParser csvParser = new CSVParser(reader, format)) {

            boolean dataStarted = false;
            DateTimeFormatter formatter = bankType.equalsIgnoreCase("MP")
                    ? DateTimeFormatter.ofPattern("dd-MM-yyyy")
                    : DateTimeFormatter.ofPattern("dd/MM/yyyy");

            UUID currentAccountId = bankType.equalsIgnoreCase("MP") ? mpId : interId;

            // Mapa para contar as ocorrências de transações idênticas no arquivo atual
            java.util.Map<String, Integer> currentFileCount = new java.util.HashMap<>();

            for (CSVRecord record : csvParser) {
                String firstCell = record.get(0);

                if (firstCell.contains("RELEASE_DATE") || firstCell.contains("Data Lançamento")) {
                    dataStarted = true;
                    continue;
                }
                if (!dataStarted || firstCell.trim().isEmpty()) continue;

                String dateStr = record.get(0);
                String description = bankType.equalsIgnoreCase("MP") ? record.get(1).trim() + " (Ref: " + record.get(2).trim() + ")" : (record.get(1) + " " + record.get(2)).trim();
                String amountStr = record.get(3);
                String balanceAfterStr = record.get(4);

                BigDecimal amount = parseCurrency(amountStr);
                BigDecimal balanceAfter = parseCurrency(balanceAfterStr);
                LocalDateTime dateTime = LocalDate.parse(dateStr.trim(), formatter).atStartOfDay();

                // Passamos o mapa de contagem para o processRow
                processRow(currentAccountId, interId, mpId, investmentId, description, amount, dateTime, bankType, balanceAfter, currentFileCount);
            }
        }
    }

    private void processRow(UUID currentAccountId, UUID interId, UUID mpId, UUID investmentId,
                            String description, BigDecimal amount, LocalDateTime date, String bankType,
                            BigDecimal balanceAfter, java.util.Map<String, Integer> currentFileCount) {

        BigDecimal absAmount = amount.abs();
        String descLower = description.toLowerCase();

        String rowKey = currentAccountId.toString() + date.toString() + absAmount.toString() + description + (balanceAfter != null ? balanceAfter.toString() : "null");
        int localCount = currentFileCount.getOrDefault(rowKey, 0) + 1;
        currentFileCount.put(rowKey, localCount);

        // Busca no banco de dados quantas vezes essa mesma transação já foi salva
        long dbCount = transactionRepositoryPort.count(currentAccountId, date, absAmount, description, balanceAfter);

        if (localCount <= dbCount) {
            return;
        }

        boolean isManualTransfer = descLower.contains("nadson") &&
                descLower.contains("santos") &&
                !descLower.contains("fatura") &&
                !descLower.contains("cartão") &&
                !descLower.contains("cartao");
        boolean isInvestment = descLower.contains("reserva") || descLower.contains("reservado") || descLower.contains("retirado");

        TransactionType type = amount.compareTo(BigDecimal.ZERO) > 0 ? TransactionType.INCOME : TransactionType.EXPENSE;

        if (isManualTransfer || isInvestment) {
            UUID destinationId;
            if (isInvestment) {
                destinationId = investmentId;
            } else {
                destinationId = (currentAccountId.equals(interId)) ? mpId : interId;
            }

            Transaction unmatched = transactionRepositoryPort.findFirstUnmatchedTransaction(currentAccountId, date, absAmount, type, destinationId);

            if (unmatched != null) {
                Transaction updatedUnmatched = new Transaction(
                        unmatched.getTransactionId(),
                        description,
                        unmatched.getAmount(),
                        unmatched.getDate(),
                        unmatched.getType(),
                        unmatched.getAccountId(),
                        unmatched.getCategoryId(),
                        unmatched.isTransfer(),
                        unmatched.getTransferID(),
                        balanceAfter
                );
                transactionRepositoryPort.save(updatedUnmatched);
            } else {
                if (amount.compareTo(BigDecimal.ZERO) < 0) {
                    transferPort.execute(currentAccountId, destinationId, absAmount, date, description, currentAccountId, balanceAfter);
                } else {
                    transferPort.execute(destinationId, currentAccountId, absAmount, date, description, currentAccountId, balanceAfter);
                }
            }
            return;
        }

        String suggestedCategoryName = CategorizationEngine.suggestCategoryName(description);
        UUID predictedCategoryId = null;

        if (suggestedCategoryName!= null) {
            Category category = categoryRepositoryPort.findByName(suggestedCategoryName);
            if (category!= null) {
                predictedCategoryId = category.getCategoryId();
            }
        }

        Transaction transaction = new Transaction(
                UUID.randomUUID(), description, absAmount, date, type, currentAccountId,
                predictedCategoryId,
                false, null, balanceAfter
        );
        createTransactionPort.execute(transaction);
    }

    private BigDecimal parseCurrency(String value) {
        try {
            String cleanValue = value.trim();
            if (cleanValue.matches(".*\\d,\\d{3}\\.\\d{2}$")) {
                NumberFormat format = NumberFormat.getInstance(Locale.US);
                return new BigDecimal(format.parse(cleanValue).toString());
            } else {
                NumberFormat format = NumberFormat.getInstance(new Locale("pt", "BR"));
                return new BigDecimal(format.parse(cleanValue).toString());
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar valor financeiro do CSV: " + value, e);
        }
    }

    private UUID findAccountIdByName(List<Account> accounts, String name) {
        return accounts.stream()
                .filter(acc -> acc.getName().toLowerCase().contains(name.toLowerCase()))
                .map(Account::getAccountId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Conta não encontrada contendo o termo: " + name));
    }
}