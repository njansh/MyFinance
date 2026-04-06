package com.nadson.myfinance.infrastructure.adapter.persistence.repository;

import com.nadson.myfinance.infrastructure.adapter.persistence.entity.TransactionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringTransactionRepository extends JpaRepository<TransactionJpaEntity, UUID> {
    List<TransactionJpaEntity> findByAccountId(UUID accountId);
}
