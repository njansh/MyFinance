package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.ConfirmRecurringPort;
import com.nadson.myfinance.application.port.in.ProcessTransactionInGoalPort;
import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.domain.enums.TransactionStatus;
import com.nadson.myfinance.domain.enums.TransactionType;
import com.nadson.myfinance.domain.exception.BusinessRuleException;
import com.nadson.myfinance.domain.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class ConfirmRecurringUseCase implements ConfirmRecurringPort {
    private final TransactionRepositoryPort transactionRepositoryPort;
    private final AccountRepositoryPort accountRepositoryPort;
    private final ProcessTransactionInGoalPort processTransactionInGoal; // Port injetado

    public ConfirmRecurringUseCase(TransactionRepositoryPort transactionRepositoryPort,
                                   AccountRepositoryPort accountRepositoryPort,
                                   ProcessTransactionInGoalPort processTransactionInGoal) { // Construtor atualizado
        this.transactionRepositoryPort = transactionRepositoryPort;
        this.accountRepositoryPort = accountRepositoryPort;
        this.processTransactionInGoal = processTransactionInGoal;
    }
    @Transactional
    @Override
    public Transaction execute(UUID userId, UUID transactionId, BigDecimal actualAmount, LocalDateTime actualDate) {
        Transaction pendingTransaction = transactionRepositoryPort.findById(transactionId);
        if (pendingTransaction == null) {
            throw new ResourceNotFoundException("Transaction not found.");
        }

        if (pendingTransaction.getStatus() == TransactionStatus.COMPLETED) {
            throw new BusinessRuleException("This transaction has already been paid.");
        }

        Account account = accountRepositoryPort.findById(pendingTransaction.getAccountId());
        if (account == null || !account.getUserId().equals(userId)) {
            throw new BusinessRuleException("Access denied: Transaction belongs to another account.");
        }

        pendingTransaction.updateDetails(
                pendingTransaction.getDescription(),
                actualAmount,
                actualDate,
                pendingTransaction.getType(),
                pendingTransaction.getAccountId(),
                pendingTransaction.getCategoryId()
        );

        pendingTransaction.markAsCompleted();

        if (pendingTransaction.getType() == TransactionType.INCOME) {
            account.deposit(actualAmount);
        } else {
            account.withdraw(actualAmount);
        }

        accountRepositoryPort.save(account);
        Transaction savedTransaction = transactionRepositoryPort.save(pendingTransaction);
        processTransactionInGoal.execute(savedTransaction);

        return savedTransaction;

    }
}
