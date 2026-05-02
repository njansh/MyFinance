package com.nadson.myfinance.infrastructure.adapter.persistence.repository;

import com.nadson.myfinance.infrastructure.adapter.persistence.entity.AccountJpaEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public interface SpringAccountRepository extends JpaRepository<AccountJpaEntity, UUID> {

    List<AccountJpaEntity> findByUserId(UUID userId);

    @Modifying
    @Transactional
    @Query("UPDATE AccountJpaEntity a SET a.balance = a.balance + :amount WHERE a.id = :id")
    void updateBalanceAtomic(@Param("id") UUID id, @Param("amount") BigDecimal amount);
}