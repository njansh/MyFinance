package com.nadson.myfinance.application.service;

import com.nadson.myfinance.application.port.in.CreateTransactionPort;
import com.nadson.myfinance.application.port.in.TransferPort;
import com.nadson.myfinance.application.port.in.ListAccountsByUserPort; // Importe sua porta aqui
import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
import com.nadson.myfinance.domain.entity.Account;
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
    private final ListAccountsByUserPort listAccountsByUserPort; // Nova porta injetada

    public TransactionImportService(TransferPort transferPort,
                                    CreateTransactionPort createTransactionPort,
                                    TransactionRepositoryPort transactionRepositoryPort,
                                    ListAccountsByUserPort listAccountsByUserPort) {
        this.transferPort = transferPort;
        this.createTransactionPort = createTransactionPort;
        this.transactionRepositoryPort = transactionRepositoryPort;
        this.listAccountsByUserPort = listAccountsByUserPort;
    }

    // Agora recebemos o userId para buscar as contas dele
    public void importCsv(MultipartFile file, String bankType, UUID userId) throws Exception {

        // 1. BUSCA AS CONTAS DO USUÁRIO DINAMICAMENTE
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
            DateTimeFormatter formatter = bankType.equals("MP")
                    ? DateTimeFormatter.ofPattern("dd-MM-yyyy")
                    : DateTimeFormatter.ofPattern("dd/MM/yyyy");

            UUID currentAccountId = bankType.equals("MP") ? mpId : interId;

            for (CSVRecord record : csvParser) {
                String firstCell = record.get(0);
                if (firstCell.contains("RELEASE_DATE") || firstCell.contains("Data Lançamento")) {
                    dataStarted = true;
                    continue;
                }
                if (!dataStarted || firstCell.trim().isEmpty()) continue;

                String dateStr = record.get(0);
                String description = bankType.equals("MP") ? record.get(1) : record.get(1) + " " + record.get(2);
                String amountStr = record.get(3);

                BigDecimal amount = new BigDecimal(amountStr.replace(".", "").replace(",", "."));
                LocalDateTime dateTime = LocalDate.parse(dateStr, formatter).atStartOfDay();

                // Passamos os IDs dinâmicos para o processamento
                processRow(currentAccountId, interId, mpId, investmentId, description, amount, dateTime, bankType);
            }
        }
    }

    // Método auxiliar para filtrar a conta pelo nome
    private UUID findAccountIdByName(List<Account> accounts, String name) {
        return accounts.stream()
                .filter(acc -> acc.getName().equalsIgnoreCase(name))
                .map(Account::getAccountId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Account not found with name: " + name));
    }

    private void processRow(UUID currentAccountId, UUID interId, UUID mpId, UUID investmentId,
                            String description, BigDecimal amount, LocalDateTime date, String bankType) {

        BigDecimal absAmount = amount.abs();
        String descLower = description.toLowerCase();

        if (transactionRepositoryPort.exists(currentAccountId, date, absAmount)) {
            return;
        }

        if (descLower.contains("nadson jhony alves nascimento santos")) {
            if (amount.compareTo(BigDecimal.ZERO) < 0) {
                UUID destinationId = bankType.equals("MP") ? interId : mpId;
                transferPort.execute(currentAccountId, destinationId, absAmount);
            }
            return;
        }

        if (descLower.contains("reserva") || descLower.contains("reservado")) {
            if (amount.compareTo(BigDecimal.ZERO) < 0) {
                transferPort.execute(currentAccountId, investmentId, absAmount);
            }
            return;
        }

        if (descLower.contains("retirado")) {
            if (amount.compareTo(BigDecimal.ZERO) > 0) {
                transferPort.execute(investmentId, currentAccountId, absAmount);
            }
            return;
        }

        TransactionType type = amount.compareTo(BigDecimal.ZERO) > 0 ? TransactionType.INCOME : TransactionType.EXPENSE;

        Transaction transaction = new Transaction(
                UUID.randomUUID(),
                description,
                absAmount,
                date,
                type,
                currentAccountId,
                null,
                false,
                null
        );

        createTransactionPort.execute(transaction);
    }
}