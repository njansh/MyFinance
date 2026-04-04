package com.nadson.myfinance.application.port.out;

import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.entity.Transaction;

import java.util.List;
import java.util.UUID;

public interface TransactionRepositoryPort {
    Transaction save(Transaction transaction);
    List<Transaction> findByAccountId(UUID accountId);
}


