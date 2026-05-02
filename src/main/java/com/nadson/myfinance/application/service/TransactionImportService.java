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
import com.nadson.myfinance.domain.enums.TransactionType;
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
    private final GetCategoriesPort getCategoriesPort;
    private final CreateCategoryPort createCategoryPort;
    private final List<CsvRowMapperStrategy> mapperStrategies;

    public TransactionImportService(TransferPort transferPort,
                                    CreateTransactionPort createTransactionPort,
                                    TransactionRepositoryPort transactionRepositoryPort,
                                    ListAccountsByUserPort listAccountsByUserPort,
                                    GetCategoriesPort getCategoriesPort,
                                    CreateCategoryPort createCategoryPort,
                                    List<CsvRowMapperStrategy> mapperStrategies) {
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

        // Proteção: Se o usuário não tiver as contas com esses nomes, o sistema avisará em vez de quebrar
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

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"));
             CSVParser csvParser = new CSVParser(reader, format)) {

            boolean dataStarted = false;
            UUID currentAccountId = bankCode.equalsIgnoreCase("MP")? mpId : interId;
            Map<String, Integer> currentFileCount = new HashMap<>();

            for (CSVRecord record : csvParser) {
                if (record.size() == 0) continue;
                String firstCell = record.get(0);

                if (firstCell.contains("RELEASE_DATE")
                        || firstCell.contains("Data Lançamento")
                        || firstCell.contains("Data")) {
                    dataStarted = true;
                    continue;
                }
                if (!dataStarted || firstCell.trim().isEmpty()) continue;

                try {
                    String description = strategy.extractDescription(record);
                    BigDecimal amount = strategy.extractAmount(record);
                    LocalDateTime dateTime = strategy.extractDate(record);
                    BigDecimal balanceAfter = strategy.extractBalanceAfter(record);

                    processRow(currentAccountId, interId, mpId, investmentId, description, amount, dateTime, balanceAfter, currentFileCount, userId, categoryCache);
                } catch (Exception e) {
                    // Ignora linhas malformadas do CSV sem parar o processo inteiro
                    continue;
                }
            }
        }
    }

    private void processRow(UUID currentAccountId, UUID interId, UUID mpId, UUID investmentId,
                            String description, BigDecimal amount, LocalDateTime date,
                            BigDecimal balanceAfter, Map<String, Integer> currentFileCount,
                            UUID userId, Map<String, UUID> categoryCache) {

        BigDecimal absAmount = amount.abs();
        String descLower = description.toLowerCase();

        String rowKey = currentAccountId.toString() + date.toString() + absAmount.toString() + description + (balanceAfter!= null? balanceAfter.toString() : "null");
        int localCount = currentFileCount.getOrDefault(rowKey, 0) + 1;
        currentFileCount.put(rowKey, localCount);

        long dbCount = transactionRepositoryPort.count(currentAccountId, date, absAmount, description, balanceAfter);
        if (localCount <= dbCount) return;

        boolean isManualTransfer = descLower.contains("nadson") && descLower.contains("santos") &&
                !descLower.contains("fatura") && !descLower.contains("cartão") && !descLower.contains("cartao");
        boolean isInvestment = descLower.contains("reserva")
                || descLower.contains("reservado")
                || descLower.contains("retirado");

        TransactionType type = amount.compareTo(BigDecimal.ZERO) > 0 ? TransactionType.INCOME : TransactionType.EXPENSE;

        if (isManualTransfer || isInvestment) {
            UUID destinationId = isInvestment? investmentId : (currentAccountId.equals(interId)? mpId : interId);
            Transaction unmatched = transactionRepositoryPort.findFirstUnmatchedTransaction(currentAccountId, date, absAmount, type, destinationId);

            if (unmatched!= null) {
                Transaction updatedUnmatched = new Transaction(unmatched.getTransactionId(), description, unmatched.getAmount(), unmatched.getDate(), unmatched.getType(), unmatched.getAccountId(), unmatched.getCategoryId(), unmatched.isTransfer(), unmatched.getTransferID(), balanceAfter);
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

        String extractedName = extractSmartCategoryName(description);
        UUID predictedCategoryId;

        if (categoryCache.containsKey(extractedName.toLowerCase())) {
            predictedCategoryId = categoryCache.get(extractedName.toLowerCase());
        } else {
            // CORREÇÃO: Gerador de cor com padding para garantir 7 caracteres (# + 6 hex)
            String randomColor = String.format("#%06x", (extractedName.hashCode() & 0xffffff));
            Category newCategory = createCategoryPort.execute(userId, extractedName, randomColor, type);
            predictedCategoryId = newCategory.getCategoryId();
            categoryCache.put(extractedName.toLowerCase(), predictedCategoryId);
        }

        Transaction transaction = new Transaction(
                UUID.randomUUID(), description, absAmount, date, type, currentAccountId,
                predictedCategoryId, false, null, balanceAfter
        );
        createTransactionPort.execute(transaction);
    }

    private String extractSmartCategoryName(String rawDescription) {
        if (rawDescription == null || rawDescription.trim().isEmpty()) return "Outros";

        String clean = rawDescription.toLowerCase()
                .replaceAll("[^a-zà-ú\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();

        if (clean.isEmpty()) return "Outros";

        String[] words = clean.split(" ");
        String mainWord = words[0];

        if (mainWord.length() <= 2 && words.length > 1) {
            mainWord = words[1];
        }

        return mainWord.substring(0, 1).toUpperCase() + mainWord.substring(1);
    }

    private UUID findAccountIdByName(List<Account> accounts, String name) {
        return accounts.stream()
                .filter(acc -> acc.getName().toLowerCase().contains(name.toLowerCase()))
                .map(Account::getAccountId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Erro: Você precisa criar uma conta chamada '" + name + "' antes de importar o CSV."));
    }
}