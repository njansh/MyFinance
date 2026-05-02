package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.CreateTransactionPort;
import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
import com.nadson.myfinance.application.port.out.CategoryRepositoryPort;
import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.entity.Category;
import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.domain.enums.TransactionType;
import jakarta.transaction.Transactional;
import com.nadson.myfinance.domain.exception.AccountNotFoundException;
import com.nadson.myfinance.domain.exception.BusinessRuleException;
import com.nadson.myfinance.domain.exception.CategoryNotFoundException;
import com.nadson.myfinance.domain.exception.InvalidTransactionValueException;

import java.math.BigDecimal;

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
    @Transactional
    public Transaction execute(Transaction transaction) {
        Account account = accountRepositoryPort.findById(transaction.getAccountId());
        if (transaction.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionValueException("Transaction amount must be greater than zero.");
        }
        if (account == null) {
            throw new AccountNotFoundException(transaction.getAccountId());
        }

        if (transaction.getCategoryId() != null) {
            Category category = categoryRepositoryPort.findById(transaction.getCategoryId());
            if (category == null) {
                throw new CategoryNotFoundException(transaction.getCategoryId());
            }
            if (category.getType() != transaction.getType()) {
                throw new BusinessRuleException("The selected category type (" + category.getType() +
                        ") does not match the transaction type (" + transaction.getType() + ")");
            }
        }

        if (transaction.getType() == TransactionType.INCOME) {
            accountRepositoryPort.updateBalanceAtomic(transaction.getAccountId(), transaction.getAmount());
        } else {
            accountRepositoryPort.updateBalanceAtomic(transaction.getAccountId(), transaction.getAmount().negate());
        }

        return transactionRepositoryPort.save(transaction);
    }
}