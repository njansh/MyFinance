package com.nadson.myfinance.application.port.out;

import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.entity.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TransactionRepositoryPort {
    Transaction save(Transaction transaction);
    Transaction findById(UUID transactionId);
    List<Transaction> findByAccountId(UUID accountId);
    List<Transaction> findByAccountIdAndDateBetween(UUID accountId, LocalDateTime startDate, LocalDateTime endDate);
}


