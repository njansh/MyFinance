package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.ProcessTransactionInGoalPort;
import com.nadson.myfinance.application.port.in.TransferPort;
import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.domain.enums.TransactionStatus;
import com.nadson.myfinance.domain.enums.TransactionType;
import com.nadson.myfinance.domain.exception.AccountNotFoundException;
import com.nadson.myfinance.domain.exception.BusinessRuleException;
import com.nadson.myfinance.domain.exception.InvalidTransactionValueException;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class TransferUseCase implements TransferPort {

    private final AccountRepositoryPort accountRepositoryPort;
    private final TransactionRepositoryPort transactionRepositoryPort;
    private final ProcessTransactionInGoalPort processTransactionInGoal; // Nova injeção

    public TransferUseCase(AccountRepositoryPort accountRepositoryPort,
                           TransactionRepositoryPort transactionRepositoryPort,
                           ProcessTransactionInGoalPort processTransactionInGoal) {
        this.accountRepositoryPort = accountRepositoryPort;
        this.transactionRepositoryPort = transactionRepositoryPort;
        this.processTransactionInGoal = processTransactionInGoal;
    }

    @Override
    @Transactional
    public void execute(UUID senderAccountId, UUID receiverAccountId, BigDecimal amount, LocalDateTime date, String description, UUID sourceAccountId, BigDecimal sourceBalanceAfter) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionValueException("Transfer amount must be greater than zero.");
        }

        Account senderAccount = accountRepositoryPort.findById(senderAccountId);
        if (senderAccount == null) throw new AccountNotFoundException(senderAccountId);

        Account receiverAccount = accountRepositoryPort.findById(receiverAccountId);
        if (receiverAccount == null) throw new AccountNotFoundException(receiverAccountId);

        if (senderAccountId.equals(receiverAccountId)) {
            throw new BusinessRuleException("Sender and receiver accounts must be different.");
        }

        accountRepositoryPort.updateBalanceAtomic(senderAccountId, amount.negate());
        accountRepositoryPort.updateBalanceAtomic(receiverAccountId, amount);

        UUID transferID = UUID.randomUUID();

        BigDecimal senderBalanceAfter = (sourceAccountId != null && sourceAccountId.equals(senderAccountId)) ? sourceBalanceAfter : null;
        BigDecimal receiverBalanceAfter = (sourceAccountId != null && sourceAccountId.equals(receiverAccountId)) ? sourceBalanceAfter : null;

        String desc = (description != null && !description.isBlank()) ? description : "Transferência";

        // 1. Cria a transação de saída (Despesa)
        Transaction debit = new Transaction(
                UUID.randomUUID(), desc, amount, date, TransactionType.EXPENSE,
                senderAccountId, null, true, transferID, senderBalanceAfter,
                TransactionStatus.COMPLETED, null
        );

        // 2. Cria a transação de entrada (Receita)
        Transaction credit = new Transaction(
                UUID.randomUUID(), desc, amount, date, TransactionType.INCOME,
                receiverAccountId, null, true, transferID, receiverBalanceAfter,
                TransactionStatus.COMPLETED, null
        );

        transactionRepositoryPort.save(debit);
        transactionRepositoryPort.save(credit);

        // 3. Processa o impacto nas metas (se houver) para ambas as contas
        processTransactionInGoal.execute(debit);
        processTransactionInGoal.execute(credit);
    }
}