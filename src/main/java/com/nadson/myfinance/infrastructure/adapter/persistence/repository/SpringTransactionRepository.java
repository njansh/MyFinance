package com.nadson.myfinance.infrastructure.adapter.persistence.repository;

import com.nadson.myfinance.infrastructure.adapter.persistence.entity.TransactionJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SpringTransactionRepository extends JpaRepository<TransactionJpaEntity, UUID> {

    Page<TransactionJpaEntity> findByAccountId(UUID accountId, Pageable pageable);

    Page<TransactionJpaEntity> findByAccountIdAndDateBetween(UUID accountId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    List<TransactionJpaEntity> findAllByAccountId(UUID accountId);

    List<TransactionJpaEntity> findAllByAccountIdAndDateBetween(UUID accountId, LocalDateTime startDate, LocalDateTime endDate);

    // 🛡️ 1. Filtro Rígido: Evita duplicar a mesma linha do mesmo extrato
    @Query("SELECT COUNT(t) > 0 FROM TransactionJpaEntity t WHERE " +
            "t.accountId = :accountId AND " +
            "t.date = :date AND " +
            "t.amount = :amount AND " +
            "t.description = :description AND " +
            "t.accountBalanceAfter = :balanceAfter")
    boolean existsWithAllFilters(
            @Param("accountId") UUID accountId,
            @Param("date") LocalDateTime date,
            @Param("amount") BigDecimal amount,
            @Param("description") String description,
            @Param("balanceAfter") BigDecimal balanceAfter
    );

     @Query("SELECT COUNT(t) > 0 FROM TransactionJpaEntity t WHERE " +
            "t.accountId = :accountId AND " +
            "t.date = :date AND " +
            "t.amount = :amount")
    boolean existsTransferCounterpart(
            @Param("accountId") UUID accountId,
            @Param("date") LocalDateTime date,
            @Param("amount") BigDecimal amount
    );

    @Query("SELECT t FROM TransactionJpaEntity t WHERE " +
            "t.accountId = :accountId AND " +
            "t.date = :date AND " +
            "t.amount = :amount")
    List<TransactionJpaEntity> findPossibleDuplicates(
            @Param("accountId") UUID accountId,
            @Param("date") LocalDateTime date,
            @Param("amount") BigDecimal amount
    );

    List<TransactionJpaEntity> findByTransferID(UUID transferID);
}