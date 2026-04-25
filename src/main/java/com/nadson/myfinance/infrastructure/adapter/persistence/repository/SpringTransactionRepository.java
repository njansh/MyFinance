package com.nadson.myfinance.infrastructure.adapter.persistence.repository;

import com.nadson.myfinance.infrastructure.adapter.persistence.entity.TransactionJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SpringTransactionRepository extends JpaRepository<TransactionJpaEntity, UUID> {
    Page<TransactionJpaEntity> findByAccountId(UUID accountId, Pageable pageable);
    Page<TransactionJpaEntity> findByAccountIdAndDateBetween(UUID accountId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    List<TransactionJpaEntity> findAllByAccountId(UUID accountId);
    List<TransactionJpaEntity> findAllByAccountIdAndDateBetween(UUID accountId, LocalDateTime startDate, LocalDateTime endDate);
    boolean existsByAccountIdAndDateAndAmount(UUID accountId, LocalDateTime date, BigDecimal amount);
    List<TransactionJpaEntity> findByTransferID(UUID transferID);
}
