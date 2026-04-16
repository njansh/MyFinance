package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.CreateTransactionPort;
import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
import com.nadson.myfinance.application.port.out.CategoryRepositoryPort;
import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.entity.Category;
import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.domain.enums.TransactionType;

public class CreateTransactionUseCase implements CreateTransactionPort {
    private final TransactionRepositoryPort transactionRepositoryPort;
    private final AccountRepositoryPort accountRepositoryPort;
    private final CategoryRepositoryPort categoryRepositoryPort;

    public CreateTransactionUseCase(
            TransactionRepositoryPort transactionRepositoryPort,
            AccountRepositoryPort accountRepositoryPort,
            CategoryRepositoryPort categoryRepositoryPort) {
        this.transactionRepositoryPort = transactionRepositoryPort;
        this.accountRepositoryPort = accountRepositoryPort;
        this.categoryRepositoryPort = categoryRepositoryPort;
    }

    @Override
    public Transaction execute(Transaction transaction) {
        Account account = accountRepositoryPort.findById(transaction.getAccountId());
        if (account == null) {
            throw new IllegalArgumentException("Account not found");
        }

        if (transaction.getCategoryId() != null) {
            Category category = categoryRepositoryPort.findById(transaction.getCategoryId());
            if (category == null) {
                throw new IllegalArgumentException("Category not found");
            }
            if (category.getType() != transaction.getType()) {
                throw new IllegalArgumentException("The selected category does not match the transaction type (Income/Expense)");
            }
        }

        if (transaction.getType() == TransactionType.INCOME) {
            account.deposit(transaction.getAmount());
        } else {
            account.withdraw(transaction.getAmount());
        }

        accountRepositoryPort.save(account);
        return transactionRepositoryPort.save(transaction);
    }
}