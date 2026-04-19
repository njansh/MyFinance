package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.ListTransactionsPort;
import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.entity.Transaction;
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
    public Page<Transaction> execute(UUID accountId, LocalDateTime startDate, LocalDateTime endDate,String description ,Pageable pageable) {
        validateAccount(accountId);
        Page<Transaction> transactionsPage;
        if (startDate != null && endDate != null) {
            transactionsPage = transactionRepositoryPort.findByAccountIdAndDateBetween(accountId, startDate, endDate, pageable);
        } else {
            transactionsPage = transactionRepositoryPort.findByAccountId(accountId, pageable);
        }
        if(description!=null && !description.isBlank()){
            List<Transaction> filteredList = transactionsPage.stream()
                    .filter(t -> t.getDescription().toLowerCase().contains(description.toLowerCase()))
                    .toList();
            return new PageImpl<>(filteredList, pageable, filteredList.size());
        }

        return transactionsPage;


    }


    private void validateAccount(UUID accountId) {
        Account account = accountRepositoryPort.findById(accountId);
        if (account == null) {
            throw new IllegalArgumentException("Account not found");
        }
    }

}
