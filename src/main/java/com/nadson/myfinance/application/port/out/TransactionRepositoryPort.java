package com.nadson.myfinance.application.port.out;

import com.nadson.myfinance.domain.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TransactionRepositoryPort {
    Transaction save(Transaction transaction);
    Transaction findById(UUID transactionId);
    Page<Transaction> findByAccountId(UUID accountId, Pageable pageable);
    Page<Transaction> findByAccountIdAndDateBetween(UUID accountId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    List<Transaction> findAllByAccountId(UUID accountId);
    List<Transaction> findAllByAccountIdAndDateBetween(UUID accountId, LocalDateTime startDate, LocalDateTime endDate);
    void deleteById(UUID transactionId);
}


