package com.nadson.myfinance.infrastructure.adapter.persistence.repository;

import com.nadson.myfinance.infrastructure.adapter.persistence.entity.BillingCycleJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface SpringBillingCycleRepository extends JpaRepository<BillingCycleJpaEntity, UUID> {
    @Modifying
    @Query("DELETE FROM BillingCycleJpaEntity b WHERE b.creditCardId IN (SELECT c.id FROM CreditCardJpaEntity c WHERE c.accountId = :accountId)")
    void deleteAllByAccountId(@Param("accountId") UUID accountId);
}
