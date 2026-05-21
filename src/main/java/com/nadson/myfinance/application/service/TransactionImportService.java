package com.nadson.myfinance.application.service;

import com.nadson.myfinance.application.parser.CsvRowMapperStrategy;
import com.nadson.myfinance.application.port.in.CreateCategoryPort;
import com.nadson.myfinance.application.port.in.CreateTransactionPort;
import com.nadson.myfinance.application.port.in.GetCategoriesPort;
import com.nadson.myfinance.application.port.in.TransferPort;
import com.nadson.myfinance.application.port.in.ListAccountsByUserPort;
import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.entity.Category;
import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.domain.enums.TransactionStatus;
import com.nadson.myfinance.domain.enums.TransactionType;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransactionImportService {
    private final CategorizationEngine categorizationEngine;
    private final TransferPort transferPort;
    private final CreateTransactionPort createTransactionPort;
    private final TransactionRepositoryPort transactionRepositoryPort;
    private final ListAccountsByUserPort listAccountsByUserPort;
    private final GetCategoriesPort getCategoriesPort;
    private final CreateCategoryPort createCategoryPort;
    private final List<CsvRowMapperStrategy> mapperStrategies;

    public TransactionImportService(CategorizationEngine categorizationEngine,
                                    TransferPort transferPort,
                                    CreateTransactionPort createTransactionPort,
                                    TransactionRepositoryPort transactionRepositoryPort,
                                    ListAccountsByUserPort listAccountsByUserPort,
                                    GetCategoriesPort getCategoriesPort,
                                    CreateCategoryPort createCategoryPort,
                                    List<CsvRowMapperStrategy> mapperStrategies) {
        this.categorizationEngine = categorizationEngine;
        this.transferPort = transferPort;
        this.createTransactionPort = createTransactionPort;
        this.transactionRepositoryPort = transactionRepositoryPort;
        this.listAccountsByUserPort = listAccountsByUserPort;
        this.getCategoriesPort = getCategoriesPort;
        this.createCategoryPort = createCategoryPort;
        this.mapperStrategies = mapperStrategies;
    }

    public void importCsv(MultipartFile file, String bankCode, UUID userId) throws Exception {
        CsvRowMapperStrategy strategy = mapperStrategies.stream()
                .filter(s -> s.getBankCode().equalsIgnoreCase(bankCode))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Banco não suportado: " + bankCode));

        List<Account> userAccounts = listAccountsByUserPort.execute(userId);

        UUID interId = findAccountIdByName(userAccounts, "Inter");
        UUID mpId = findAccountIdByName(userAccounts, "Mercado Pago");
        UUID investmentId = findAccountIdByName(userAccounts, "Investimento");

        List<Category> userCategories = getCategoriesPort.execute(userId);
        Map<String, UUID> categoryCache = new HashMap<>();
        for (Category cat : userCategories) {
            categoryCache.put(cat.getName().toLowerCase(), cat.getCategoryId());
        }

        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setDelimiter(';')
                .setIgnoreEmptyLines(true)
                .build();

        List<CsvRowData> rowsToProcess = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"));
             CSVParser csvParser = new CSVParser(reader, format)) {

            boolean dataStarted = false;
            for (CSVRecord record : csvParser) {
                if (record.size() == 0) continue;
                String firstCell = record.get(0);

                if (firstCell.contains("RELEASE_DATE") || firstCell.contains("Data Lançamento") || firstCell.contains("Data")) {
                    dataStarted = true;
                    continue;
                }
                if (!dataStarted || firstCell.trim().isEmpty()) continue;

                try {
                    rowsToProcess.add(new CsvRowData(
                            strategy.extractDescription(record),
                            strategy.extractAmount(record),
                            strategy.extractDate(record),
                            strategy.extractBalanceAfter(record)
                    ));
                } catch (Exception e) {
                    continue;
                }
            }
        }

        if (rowsToProcess.isEmpty()) return;

        LocalDateTime minDate = rowsToProcess.stream().map(CsvRowData::date).min(LocalDateTime::compareTo).orElseThrow();
        LocalDateTime maxDate = rowsToProcess.stream().map(CsvRowData::date).max(LocalDateTime::compareTo).orElseThrow();
        UUID currentAccountId = bankCode.equalsIgnoreCase("MP") ? mpId : interId;

        List<Transaction> existingTransactions = transactionRepositoryPort.findAllByAccountIdAndDateBetween(currentAccountId, minDate, maxDate);
        List<String> existingHashes = existingTransactions.stream()
                .map(this::generateHash)
                .collect(Collectors.toList());

        Map<String, Integer> currentFileCount = new HashMap<>();

        for (CsvRowData row : rowsToProcess) {
            processRow(currentAccountId, interId, mpId, investmentId, row, currentFileCount, existingHashes, userId, categoryCache);
        }
    }

    private void processRow(UUID currentAccountId, UUID interId, UUID mpId, UUID investmentId,
                            CsvRowData row, Map<String, Integer> currentFileCount,
                            List<String> existingHashes, UUID userId, Map<String, UUID> categoryCache) {

        BigDecimal absAmount = row.amount().abs();
        String hash = generateHash(currentAccountId, row.date(), absAmount, row.description(), row.balanceAfter());
        
        int localCount = currentFileCount.getOrDefault(hash, 0) + 1;
        currentFileCount.put(hash, localCount);

        // Se o hash já existe no DB, precisamos contar quantas vezes ele existe para lidar com duplicatas legítimas no mesmo arquivo
        long dbOccurrences = existingHashes.stream().filter(h -> h.equals(hash)).count();
        if (localCount <= dbOccurrences) return;

        String descLower = row.description().toLowerCase();
        boolean isManualTransfer = descLower.contains("nadson") && descLower.contains("santos") &&
                !descLower.contains("fatura") && !descLower.contains("cartão") && !descLower.contains("cartao");
        boolean isInvestment = descLower.contains("reserva") || descLower.contains("reservado") || descLower.contains("retirado");

        TransactionType type = row.amount().compareTo(BigDecimal.ZERO) > 0 ? TransactionType.INCOME : TransactionType.EXPENSE;

        if (isManualTransfer || isInvestment) {
            UUID destinationId = isInvestment ? investmentId : (currentAccountId.equals(interId) ? mpId : interId);
            Transaction unmatched = transactionRepositoryPort.findFirstUnmatchedTransaction(currentAccountId, row.date(), absAmount, type, destinationId);

            if (unmatched != null) {
                Transaction updatedUnmatched = new Transaction(unmatched.getTransactionId(), row.description(), unmatched.getAmount(), unmatched.getDate(), unmatched.getType(), unmatched.getAccountId(), unmatched.getCategoryId(), unmatched.isTransfer(), unmatched.getTransferID(), row.balanceAfter(), unmatched.getStatus());
                transactionRepositoryPort.save(updatedUnmatched);
            } else {
                if (row.amount().compareTo(BigDecimal.ZERO) < 0) {
                    transferPort.execute(currentAccountId, destinationId, absAmount, row.date(), row.description(), currentAccountId, row.balanceAfter());
                } else {
                    transferPort.execute(destinationId, currentAccountId, absAmount, row.date(), row.description(), currentAccountId, row.balanceAfter());
                }
            }
            return;
        }

        String extractedName = categorizationEngine.process(row.description());
        UUID predictedCategoryId;

        if (categoryCache.containsKey(extractedName.toLowerCase())) {
            predictedCategoryId = categoryCache.get(extractedName.toLowerCase());
        } else {
            String randomColor = String.format("#%06x", (extractedName.hashCode() & 0xffffff));
            Category newCategory = createCategoryPort.execute(userId, extractedName, randomColor, type);
            predictedCategoryId = newCategory.getCategoryId();
            categoryCache.put(extractedName.toLowerCase(), predictedCategoryId);
        }

        Transaction transaction = new Transaction(
                UUID.randomUUID(), row.description(), absAmount, row.date(), type, currentAccountId,
                predictedCategoryId, false, null, row.balanceAfter(), TransactionStatus.COMPLETED
        );
        createTransactionPort.execute(transaction);
    }

    private String generateHash(Transaction t) {
        return generateHash(t.getAccountId(), t.getDate(), t.getAmount(), t.getDescription(), t.getAccountBalanceAfter());
    }

    private String generateHash(UUID accountId, LocalDateTime date, BigDecimal amount, String description, BigDecimal balanceAfter) {
        return accountId.toString() + "_" + date.toString() + "_" + amount.toString() + "_" + description + "_" + (balanceAfter != null ? balanceAfter.toString() : "null");
    }

    private UUID findAccountIdByName(List<Account> accounts, String name) {
        return accounts.stream()
                .filter(acc -> acc.getName().toLowerCase().contains(name.toLowerCase()))
                .map(Account::getAccountId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Error: You must create an account named '" + name + "' before importing the CSV."));
    }

    private record CsvRowData(String description, BigDecimal amount, LocalDateTime date, BigDecimal balanceAfter) {}
}
