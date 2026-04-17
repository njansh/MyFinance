package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.UpdateTransactionPort;
import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
import com.nadson.myfinance.application.port.out.CategoryRepositoryPort;
import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
import com.nadson.myfinance.application.port.out.UserRepositoryPort;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.domain.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class UpdateTransactionUseCase implements UpdateTransactionPort {
    private final TransactionRepositoryPort transactionRepositoryPort;
    private final CategoryRepositoryPort categoryRepositoryPort;
    private final AccountRepositoryPort accountRepositoryPort;

    public UpdateTransactionUseCase(TransactionRepositoryPort transactionRepositoryPort, CategoryRepositoryPort categoryRepositoryPort, AccountRepositoryPort accountRepositoryPort) {
        this.transactionRepositoryPort = transactionRepositoryPort;
        this.categoryRepositoryPort = categoryRepositoryPort;
        this.accountRepositoryPort = accountRepositoryPort;
    }

    @Override
    public void execute(UUID transactionId, String description, BigDecimal amount, LocalDateTime date, TransactionType type, UUID accountId, UUID categoryId) {
        Transaction transaction = this.transactionRepositoryPort.findById(transactionId);
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction not found");
        }
        if (categoryId != null && this.categoryRepositoryPort.findById(categoryId) == null) {
            throw new IllegalArgumentException("Category not found");
        }
        Account oldAccount=this.accountRepositoryPort.findById(transaction.getAccountId());
        if (oldAccount == null) {
            throw new IllegalArgumentException("Account not found");
        }
        if (transaction.getType() == TransactionType.EXPENSE) {
            oldAccount.deposit(transaction.getAmount());
        } else {
            oldAccount.withdraw(transaction.getAmount());
        }
        accountRepositoryPort.save(oldAccount);
        transaction.updateDetails(description, amount, date, type, accountId, categoryId);
        Account currentAccount = accountRepositoryPort.findById(transaction.getAccountId());
        if (transaction.getType() == TransactionType.EXPENSE) {
            currentAccount.withdraw(transaction.getAmount());
            } else {
            currentAccount.deposit(transaction.getAmount());
        }
        accountRepositoryPort.save(currentAccount);
        transactionRepositoryPort.save(transaction);



    }
}
