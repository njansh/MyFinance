package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.CreateTransactionPort;
import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.entity.Transaction;

public class CreateTransactionUseCase implements CreateTransactionPort {
    private final TransactionRepositoryPort transactionRepositoryPort;
    private final AccountRepositoryPort accountRepositoryPort;

    public CreateTransactionUseCase(TransactionRepositoryPort transactionRepositoryPort, AccountRepositoryPort accountRepositoryPort) {
        this.transactionRepositoryPort = transactionRepositoryPort;
        this.accountRepositoryPort = accountRepositoryPort;
    }

    @Override
    public Transaction execute(Transaction transaction) {
        Account account = accountRepositoryPort.findById(transaction.getAccountId());
        if (account == null) {
            throw new IllegalArgumentException("Account not found");
        }
        return transactionRepositoryPort.save(transaction);
    }
}
