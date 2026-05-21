package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.CreateTransactionPort;
import com.nadson.myfinance.application.port.in.ProcessTransactionInBudgetPort; // Import adicionado
import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
import com.nadson.myfinance.application.port.out.CategoryRepositoryPort;
import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.entity.Category;
import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.domain.enums.TransactionType;
import com.nadson.myfinance.domain.exception.*;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;

public class CreateTransactionUseCase implements CreateTransactionPort {
    private final TransactionRepositoryPort transactionRepositoryPort;
    private final AccountRepositoryPort accountRepositoryPort;
    private final CategoryRepositoryPort categoryRepositoryPort;
    private final ProcessTransactionInBudgetPort processTransactionInBudget; // Nova dependência

    public CreateTransactionUseCase(
            TransactionRepositoryPort transactionRepositoryPort,
            AccountRepositoryPort accountRepositoryPort,
            CategoryRepositoryPort categoryRepositoryPort,
            ProcessTransactionInBudgetPort processTransactionInBudget) {
        this.transactionRepositoryPort = transactionRepositoryPort;
        this.accountRepositoryPort = accountRepositoryPort;
        this.categoryRepositoryPort = categoryRepositoryPort;
        this.processTransactionInBudget = processTransactionInBudget;
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
                throw new BusinessRuleException("The selected category type does not match the transaction type");
            }
        }


        if (transaction.getType() == TransactionType.INCOME) {
            accountRepositoryPort.updateBalanceAtomic(transaction.getAccountId(), transaction.getAmount());
        } else {
            accountRepositoryPort.updateBalanceAtomic(transaction.getAccountId(), transaction.getAmount().negate());
        }


        Transaction savedTransaction = transactionRepositoryPort.save(transaction);

        processTransactionInBudget.execute(savedTransaction);

        return savedTransaction;
    }
}