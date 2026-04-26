package com.nadson.myfinance.application.port.out;

import com.nadson.myfinance.domain.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepositoryPort {
    Transaction save(Transaction transaction);

    Transaction findById(UUID transactionId);

    List<Transaction> findAllByTransferID(UUID transferID);

    Page<Transaction> findByAccountId(UUID accountId, Pageable pageable);

    Page<Transaction> findByAccountIdAndDateBetween(UUID accountId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    List<Transaction> findAllByAccountId(UUID accountId);

    List<Transaction> findAllByAccountIdAndDateBetween(UUID accountId, LocalDateTime startDate, LocalDateTime endDate);

    long count(UUID accountId, LocalDateTime date, BigDecimal amount, String description, BigDecimal accountBalanceAfter);
    void deleteById(UUID transactionId);

    List<Transaction> findPossibleDuplicates(UUID accountId, LocalDateTime date, BigDecimal amount);

    void updateBalance(UUID transactionId, BigDecimal balanceAfter);

    boolean existsTransferCounterpart(UUID accountId, LocalDateTime date, BigDecimal amount);
    Page<Transaction> findByAccountIdAndDescription(UUID accountId, String description, Pageable pageable);

    Page<Transaction> findByAccountIdAndDateBetweenAndDescription(UUID accountId, LocalDateTime startDate, LocalDateTime endDate, String description, Pageable pageable);
    java.util.Map<String, BigDecimal> getSumByCategoryAndType(UUID accountId, com.nadson.myfinance.domain.enums.TransactionType type);

    java.util.Map<String, BigDecimal> getSumByCategoryAndTypeAndDateBetween(UUID accountId, com.nadson.myfinance.domain.enums.TransactionType type, LocalDateTime startDate, LocalDateTime endDate);
    Transaction findFirstUnmatchedTransaction(UUID accountId, LocalDateTime date, BigDecimal amount);

}


