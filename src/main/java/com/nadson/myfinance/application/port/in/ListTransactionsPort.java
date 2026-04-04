package com.nadson.myfinance.application.port.in;

import com.nadson.myfinance.domain.entity.Transaction;

import java.util.List;
import java.util.UUID;

public interface ListTransactionsPort {
    List<Transaction> execute(UUID accountId);
}
    
