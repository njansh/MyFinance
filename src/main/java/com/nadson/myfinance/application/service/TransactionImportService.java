package com.nadson.myfinance.application.service;

import com.nadson.myfinance.application.parser.CsvRowMapperStrategy;
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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class TransactionImportService {

    private final TransferPort transferPort;
    private final CreateTransactionPort createTransactionPort;
    private final TransactionRepositoryPort transactionRepositoryPort;
    private final ListAccountsByUserPort listAccountsByUserPort;
    private final CategoryRepositoryPort categoryRepositoryPort;
    private final List<CsvRowMapperStrategy> mapperStrategies;

    public TransactionImportService(TransferPort transferPort,
                                    CreateTransactionPort createTransactionPort,
                                    TransactionRepositoryPort transactionRepositoryPort,
                                    ListAccountsByUserPort listAccountsByUserPort,
                                    CategoryRepositoryPort categoryRepositoryPort,
                                    List<CsvRowMapperStrategy> mapperStrategies) {
        this.transferPort = transferPort;
        this.createTransactionPort = createTransactionPort;
        this.transactionRepositoryPort = transactionRepositoryPort;
        this.listAccountsByUserPort = listAccountsByUserPort;
        this.categoryRepositoryPort = categoryRepositoryPort;
        this.mapperStrategies = mapperStrategies;
    }

    public void importCsv(MultipartFile file, String bankCode, UUID userId) throws Exception {
        // 1. Delegação: Pega a estratégia correta dinamicamente
        CsvRowMapperStrategy strategy = mapperStrategies.stream()
                .filter(s -> s.getBankCode().equalsIgnoreCase(bankCode))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Banco não suportado ou ainda não implementado: " + bankCode));

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
            UUID currentAccountId = bankCode.equalsIgnoreCase("MP")? mpId : interId;

            // Mapa para contar as ocorrências de transações idênticas no arquivo atual
            Map<String, Integer> currentFileCount = new HashMap<>();

            for (CSVRecord record : csvParser) {
                String firstCell = record.get(0);

                // Pula o cabeçalho
                if (firstCell.contains("RELEASE_DATE") || firstCell.contains("Data Lançamento") || firstCell.contains("Data")) {
                    dataStarted = true;
                    continue;
                }
                if (!dataStarted || firstCell.trim().isEmpty()) continue;

                String description = strategy.extractDescription(record);
                BigDecimal amount = strategy.extractAmount(record);
                LocalDateTime dateTime = strategy.extractDate(record);
                BigDecimal balanceAfter = strategy.extractBalanceAfter(record);

                processRow(currentAccountId, interId, mpId, investmentId, description, amount, dateTime, balanceAfter, currentFileCount, userId);
            }
        }
    }

    private void processRow(UUID currentAccountId, UUID interId, UUID mpId, UUID investmentId,
                            String description, BigDecimal amount, LocalDateTime date,
                            BigDecimal balanceAfter, Map<String, Integer> currentFileCount, UUID userId) {

        BigDecimal absAmount = amount.abs();
        String descLower = description.toLowerCase();

        String rowKey = currentAccountId.toString() + date.toString() + absAmount.toString() + description + (balanceAfter!= null? balanceAfter.toString() : "null");
        int localCount = currentFileCount.getOrDefault(rowKey, 0) + 1;
        currentFileCount.put(rowKey, localCount);

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

        TransactionType type = amount.compareTo(BigDecimal.ZERO) > 0? TransactionType.INCOME : TransactionType.EXPENSE;

        if (isManualTransfer || isInvestment) {
            UUID destinationId;
            if (isInvestment) {
                destinationId = investmentId;
            } else {
                destinationId = (currentAccountId.equals(interId))? mpId : interId;
            }

            Transaction unmatched = transactionRepositoryPort.findFirstUnmatchedTransaction(currentAccountId, date, absAmount, type, destinationId);

            if (unmatched!= null) {
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
            // CORREÇÃO: Agora passamos o suggestedCategoryName e o userId
            Category category = categoryRepositoryPort.findByNameAndUserId(suggestedCategoryName, userId);
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

    private UUID findAccountIdByName(List<Account> accounts, String name) {
        return accounts.stream()
                .filter(acc -> acc.getName().toLowerCase().contains(name.toLowerCase()))
                .map(Account::getAccountId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Conta não encontrada contendo o termo: " + name));
    }
}