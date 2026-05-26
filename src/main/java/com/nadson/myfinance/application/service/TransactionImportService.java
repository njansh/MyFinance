package com.nadson.myfinance.application.service;

import com.nadson.myfinance.application.parser.CsvRowMapperStrategy;
import com.nadson.myfinance.application.port.in.*;
import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.entity.Category;
import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.domain.enums.TransactionStatus;
import com.nadson.myfinance.domain.enums.TransactionType;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransactionImportService {
    private static final Logger log = LoggerFactory.getLogger(TransactionImportService.class);

    private final CategorizationEngine categorizationEngine;
    private final TransferPort transferPort;
    private final CreateTransactionPort createTransactionPort;
    private final TransactionRepositoryPort transactionRepositoryPort;
    private final ListAccountsByUserPort listAccountsByUserPort;
    private final GetCategoriesPort getCategoriesPort;
    private final CreateCategoryPort createCategoryPort;
    private final List<CsvRowMapperStrategy> mapperStrategies;

    public TransactionImportService(CategorizationEngine categorizationEngine, TransferPort transferPort,
                                    CreateTransactionPort createTransactionPort, TransactionRepositoryPort transactionRepositoryPort,
                                    ListAccountsByUserPort listAccountsByUserPort, GetCategoriesPort getCategoriesPort,
                                    CreateCategoryPort createCategoryPort, List<CsvRowMapperStrategy> mapperStrategies) {
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
        try (InputStream is = file.getInputStream()) { processCsvStream(is, bankCode, userId); }
    }

    public void processFile(File file, String bankCode, UUID userId) throws Exception {
        try (InputStream is = new FileInputStream(file)) { processCsvStream(is, bankCode, userId); }
    }

    private void processCsvStream(InputStream is, String bankCode, UUID userId) throws Exception {
        CsvRowMapperStrategy strategy = mapperStrategies.stream()
                .filter(s -> s.getBankCode().equalsIgnoreCase(bankCode))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Banco não suportado: " + bankCode));

        List<Account> userAccounts = listAccountsByUserPort.execute(userId);

        UUID currentAccountId = getAccountIdByBankCode(userAccounts, bankCode);
        UUID interId = getAccountIdByBankCode(userAccounts, "INTER");
        UUID mpId = getAccountIdByBankCode(userAccounts, "MP");
        UUID investmentId = getAccountIdByBankCode(userAccounts, "INVESTIMENTO");

        log.info("Processando importação. Banco: {}. Conta alvo ID: {}", bankCode, currentAccountId);

        List<Category> userCategories = getCategoriesPort.execute(userId);
        Map<String, Category> categoryCache = new HashMap<>();
        for (Category cat : userCategories) {
            // Cache by name and type to ensure correct category usage
            categoryCache.put(cat.getName().toLowerCase() + "_" + cat.getType(), cat);
        }

        List<CsvRowData> rowsToProcess = new ArrayList<>();
        CSVFormat format = CSVFormat.DEFAULT.builder().setDelimiter(';').setIgnoreEmptyLines(true).build();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
             CSVParser csvParser = new CSVParser(reader, format)) {
            boolean dataStarted = false;
            for (CSVRecord record : csvParser) {
                if (record.size() < 2) continue;
                String first = record.get(0);
                if (first.contains("RELEASE_DATE") || first.contains("Data")) { dataStarted = true; continue; }
                if (!dataStarted || first.trim().isEmpty()) continue;

                rowsToProcess.add(new CsvRowData(strategy.extractDescription(record), strategy.extractAmount(record),
                        strategy.extractDate(record), strategy.extractBalanceAfter(record), strategy.extractReferenceId(record)));
            }
        }
        LocalDateTime minDate = rowsToProcess.stream()
                .map(CsvRowData::date)
                .min(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now().minusYears(2));

        LocalDateTime maxDate = rowsToProcess.stream()
                .map(CsvRowData::date)
                .max(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now().plusDays(1));

        List<Transaction> existingTransactions = transactionRepositoryPort.findAllByAccountIdAndDateBetween(
                currentAccountId, minDate.minusDays(1), maxDate.plusDays(1));

        Set<String> processedHashes = existingTransactions.stream()
                .map(t -> generateHash(currentAccountId, t.getDate(), t.getAmount(), t.getDescription(), t.getAccountBalanceAfter(), null))
                .collect(Collectors.toSet());

        // MAPA DE FOOTPRINT: Agora usamos o Tipo e contamos quantas transferências idênticas existem
        Map<String, Integer> transferFootprints = new HashMap<>();
        for (Transaction t : existingTransactions) {
            if (t.isTransfer()) {
                String footprint = t.getType().name() + "_" + t.getDate().toLocalDate().toString() + "_" + t.getAmount().abs().setScale(2, RoundingMode.HALF_UP);
                transferFootprints.put(footprint, transferFootprints.getOrDefault(footprint, 0) + 1);
            }
        }

        for (CsvRowData row : rowsToProcess) {
            String desc = row.description().toLowerCase();
            boolean isTransferLine = isTransferDescription(desc);

            // TIPO 100% BASEADO NO SINAL MATEMÁTICO DO BANCO (Sem "adivinhar" por palavras)
            TransactionType type = row.amount().compareTo(BigDecimal.ZERO) > 0 ? TransactionType.INCOME : TransactionType.EXPENSE;

            String hash = generateHash(currentAccountId, row.date(), row.amount(), row.description(), row.balanceAfter(), bankCode);
            String footprint = type.name() + "_" + row.date().toLocalDate().toString() + "_" + row.amount().abs().setScale(2, RoundingMode.HALF_UP);

            if (processedHashes.contains(hash)) continue;

            // Deduplicação Cruzada (Inter x MP) com base na contagem
            if (isTransferLine && transferFootprints.getOrDefault(footprint, 0) > 0) {
                log.info("Transferência ignorada (já processada via contrapartida): {} | R$ {}", row.description(), row.amount().abs());
                transferFootprints.put(footprint, transferFootprints.get(footprint) - 1);
                continue;
            }

            processRow(currentAccountId, interId, mpId, investmentId, row, userId, categoryCache, isTransferLine, type);

            processedHashes.add(hash);
        }
    }

    private boolean isTransferDescription(String desc) {
        // MUITO MAIS RESTRITO: Removemos "transferência" solto.
        return desc.contains("transferência entre contas") ||
                desc.contains("reserva") ||
                desc.contains("retirado") ||
                desc.contains("nadson jhony") ||
                desc.contains("poupança") ||
                desc.contains("resgate") ||
                desc.contains("investimento");
    }

    private UUID getAccountIdByBankCode(List<Account> accounts, String code) {
        String nameToFind = code.equalsIgnoreCase("MP") ? "Mercado Pago" : code;
        return accounts.stream()
                .filter(a -> a.getName().equalsIgnoreCase(nameToFind))
                .map(Account::getAccountId)
                .findFirst()
                .orElse(UUID.randomUUID());
    }

    private void processRow(UUID currentAccountId, UUID interId, UUID mpId, UUID investmentId,
                            CsvRowData row, UUID userId, Map<String, Category> categoryCache, boolean isTransfer, TransactionType type) {

        BigDecimal absAmount = row.amount().abs();

        if (isTransfer) {
            log.info("Processando linha como TRANSFERÊNCIA: {}", row.description());

            UUID otherAccount = (row.description().toLowerCase().contains("reserva") ||
                    row.description().toLowerCase().contains("retirado") ||
                    row.description().toLowerCase().contains("investimento") ||
                    row.description().toLowerCase().contains("poupança") ||
                    row.description().toLowerCase().contains("resgate"))
                    ? investmentId
                    : (currentAccountId.equals(interId) ? mpId : interId);

            UUID senderId;
            UUID receiverId;

            if (type == TransactionType.EXPENSE) {
                senderId = currentAccountId;
                receiverId = otherAccount;
            } else {
                senderId = otherAccount;
                receiverId = currentAccountId;
            }

            transferPort.execute(senderId, receiverId, absAmount, row.date(), row.description(), currentAccountId, row.balanceAfter());
        } else {
            log.info("Processando linha como TRANSAÇÃO COMUM: {}", row.description());

            String catName = categorizationEngine.process(row.description());
            String catKey = catName.toLowerCase() + "_" + type;

            Category category = categoryCache.get(catKey);
            UUID catId;

            if (category == null) {
                category = createCategoryPort.execute(userId, catName, generateRandomColor(), "Circle", type);
                categoryCache.put(catKey, category);
            }
            catId = category.getCategoryId();

            createTransactionPort.execute(new Transaction(UUID.randomUUID(), row.description(), absAmount, row.date(), type, currentAccountId, catId, false, null, row.balanceAfter(), TransactionStatus.COMPLETED, null));
        }
    }
    private String generateRandomColor() {
        Random obj = new Random();
        int randNum = obj.nextInt(0xffffff + 1);
        return String.format("#%06x", randNum);
    }

    private String generateHash(UUID accId, LocalDateTime date, BigDecimal amt, String desc, BigDecimal balanceAfter, String bankCode) {
        String cleanDesc = (desc != null) ? desc.trim().toLowerCase().replaceAll("\\s+", " ") : "n/a";
        String balanceStr = (balanceAfter != null) ? balanceAfter.setScale(2, RoundingMode.HALF_UP).toString() : "0.00";
        return bankCode + "_" + accId.toString() + "_" + date.toLocalDate() + "_" +
                amt.abs().setScale(2, RoundingMode.HALF_UP) + "_" + cleanDesc + "_" + balanceStr;
    }

    private record CsvRowData(String description, BigDecimal amount, LocalDateTime date, BigDecimal balanceAfter, String referenceId) {}
}
