package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.UpdateTransactionPort;
import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
import com.nadson.myfinance.application.port.out.CategoryRepositoryPort;
import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.domain.enums.TransactionType;
import com.nadson.myfinance.domain.exception.AccountNotFoundException;
import com.nadson.myfinance.domain.exception.CategoryNotFoundException;
import com.nadson.myfinance.domain.exception.TransactionNotFoundException;

import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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
    @Transactional
    public void execute(UUID transactionId, String description, BigDecimal amount, LocalDateTime date, TransactionType type, UUID accountId, UUID categoryId) {
        Transaction transaction = this.transactionRepositoryPort.findById(transactionId);
        if (transaction == null) throw new TransactionNotFoundException(transactionId);

        if (categoryId != null && this.categoryRepositoryPort.findById(categoryId) == null) {
            throw new CategoryNotFoundException(categoryId);
        }

        if (transaction.isTransfer()) {
            updateTransfer(transaction, description, amount, date);
        } else {
            updateRegularTransaction(transaction, description, amount, date, type, accountId, categoryId);
        }
    }

    private void updateRegularTransaction(Transaction transaction, String description, BigDecimal amount, LocalDateTime date, TransactionType type, UUID accountId, UUID categoryId) {
        Account oldAccount = this.accountRepositoryPort.findById(transaction.getAccountId());
        if (oldAccount == null) throw new AccountNotFoundException(transaction.getAccountId());

        if (transaction.getType() == TransactionType.EXPENSE) {
            oldAccount.deposit(transaction.getAmount());
        } else {
            oldAccount.withdraw(transaction.getAmount());
        }
        accountRepositoryPort.save(oldAccount);

        transaction.updateDetails(description, amount, date, type, accountId, categoryId);

        Account currentAccount = accountRepositoryPort.findById(transaction.getAccountId());
        if (currentAccount == null) throw new AccountNotFoundException(transaction.getAccountId());

        // Aplica o novo saldo
        if (transaction.getType() == TransactionType.EXPENSE) {
            currentAccount.withdraw(transaction.getAmount());
        } else {
            currentAccount.deposit(transaction.getAmount());
        }

        accountRepositoryPort.save(currentAccount);
        transactionRepositoryPort.save(transaction);
    }

    private void updateTransfer(Transaction transaction, String description, BigDecimal amount, LocalDateTime date) {
        List<Transaction> transferParts = transactionRepositoryPort.findAllByTransferID(transaction.getTransferID());

        for (Transaction part : transferParts) {
            Account account = accountRepositoryPort.findById(part.getAccountId());
            if (account == null) throw new AccountNotFoundException(part.getAccountId());

            if (part.getType() == TransactionType.EXPENSE) {
                account.deposit(part.getAmount());
            } else {
                account.withdraw(part.getAmount());
            }

            part.updateDetails(description, amount, date, part.getType(), part.getAccountId(), part.getCategoryId());

            if (part.getType() == TransactionType.EXPENSE) {
                account.withdraw(part.getAmount());
            } else {
                account.deposit(part.getAmount());
            }

            accountRepositoryPort.save(account);
            transactionRepositoryPort.save(part);
        }
    }
}