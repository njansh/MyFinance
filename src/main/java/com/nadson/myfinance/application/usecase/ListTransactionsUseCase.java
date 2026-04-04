package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.ListTransactionsPort;
import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.entity.Transaction;

import java.util.List;
import java.util.UUID;

public class ListTransactionsUseCase implements ListTransactionsPort {
    private final AccountRepositoryPort accountRepositoryPort;
    private final TransactionRepositoryPort transactionRepositoryPort;

    public ListTransactionsUseCase(AccountRepositoryPort accountRepositoryPort, TransactionRepositoryPort transactionRepositoryPort) {
        this.accountRepositoryPort = accountRepositoryPort;
        this.transactionRepositoryPort = transactionRepositoryPort;
    }

    @Override
    public List<Transaction> execute(UUID accountId) {
        Account account = accountRepositoryPort.findById(accountId);
        if (account == null) {
            throw new IllegalArgumentException("Account not found");
        }

        return transactionRepositoryPort.findByAccountId(accountId);
    }
}
