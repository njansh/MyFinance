package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.ListTransactionsPort;
import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.domain.exception.AccountNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
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
    public Page<Transaction> execute(UUID accountId, LocalDateTime startDate, LocalDateTime endDate, String description, Pageable pageable) {
        validateAccount(accountId);

        boolean hasDescription = description!= null &&!description.isBlank();

        if (startDate!= null && endDate!= null) {
            if (hasDescription) {
                return transactionRepositoryPort.findByAccountIdAndDateBetweenAndDescription(accountId, startDate, endDate, description, pageable);
            }
            return transactionRepositoryPort.findByAccountIdAndDateBetween(accountId, startDate, endDate, pageable);
        } else {
            if (hasDescription) {
                return transactionRepositoryPort.findByAccountIdAndDescription(accountId, description, pageable);
            }
            return transactionRepositoryPort.findByAccountId(accountId, pageable);
        }
    }

    private void validateAccount(UUID accountId) {
        Account account = accountRepositoryPort.findById(accountId);
        if (account == null) {
            throw new AccountNotFoundException(accountId);
        }
    }

}
