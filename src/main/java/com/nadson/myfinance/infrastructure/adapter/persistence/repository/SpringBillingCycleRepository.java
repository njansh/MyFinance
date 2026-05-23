package com.nadson.myfinance.infrastructure.adapter.persistence.repository;

import com.nadson.myfinance.domain.enums.BillingCycleStatus;
import com.nadson.myfinance.infrastructure.adapter.persistence.entity.BillingCycleJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface SpringBillingCycleRepository extends JpaRepository<BillingCycleJpaEntity, UUID> {
    List<BillingCycleJpaEntity> findByCreditCardId(UUID creditCardId);
    List<BillingCycleJpaEntity> findByCreditCardIdAndStatus(UUID creditCardId, BillingCycleStatus status);

    @Modifying
    @Query("DELETE FROM BillingCycleJpaEntity b WHERE b.creditCardId IN (SELECT c.id FROM CreditCardJpaEntity c WHERE c.accountId = :accountId)")
    void deleteAllByAccountId(@Param("accountId") UUID accountId);
    List<BillingCycleJpaEntity> findByCreditCardIdAndDueDateBetween(UUID creditCardId, LocalDate start, LocalDate end);

    @Modifying
    @Query("DELETE FROM BillingCycleJpaEntity b WHERE b.creditCardId IN (SELECT c.id FROM CreditCardJpaEntity c WHERE c.userId = :userId)")
    void deleteAllByUserId(@Param("userId") UUID userId);

    @Modifying
    @Query("DELETE FROM BillingCycleJpaEntity b WHERE b.creditCardId = :creditCardId")
    void deleteAllByCreditCardId(@Param("creditCardId") UUID creditCardId);
}
