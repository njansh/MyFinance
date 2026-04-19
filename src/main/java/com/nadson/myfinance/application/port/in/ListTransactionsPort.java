package com.nadson.myfinance.application.port.in;

import com.nadson.myfinance.domain.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.UUID;

public interface ListTransactionsPort {

    Page<Transaction> execute(UUID accountId, LocalDateTime startDate, LocalDateTime endDate,String description ,Pageable pageable);
}

