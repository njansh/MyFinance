package com.nadson.myfinance.application.port.in;

import com.nadson.myfinance.domain.entity.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ListTransactionsPort {
    List<Transaction> execute(UUID accountId);
    List<Transaction> execute(UUID accountId, LocalDateTime startDate, LocalDateTime endDate);
}
    
