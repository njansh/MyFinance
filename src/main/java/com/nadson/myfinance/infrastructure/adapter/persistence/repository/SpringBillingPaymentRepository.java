package com.nadson.myfinance.infrastructure.adapter.persistence.repository;

import com.nadson.myfinance.infrastructure.adapter.persistence.entity.BillingPaymentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SpringBillingPaymentRepository extends JpaRepository<BillingPaymentJpaEntity, UUID> {
    @Modifying
    @Query("DELETE FROM BillingPaymentJpaEntity p WHERE p.billingCycleId IN " +
            "(SELECT c.id FROM BillingCycleJpaEntity c WHERE c.creditCardId IN " +
            "(SELECT cc.id FROM CreditCardJpaEntity cc WHERE cc.accountId = :accountId))")
    void deleteByAccountId(@Param("accountId") UUID accountId);

    @Modifying
    @Query("DELETE FROM BillingPaymentJpaEntity p WHERE p.billingCycleId IN " +
            "(SELECT c.id FROM BillingCycleJpaEntity c WHERE c.creditCardId IN " +
            "(SELECT cc.id FROM CreditCardJpaEntity cc WHERE cc.userId = :userId))")
    void deleteAllByUserId(@Param("userId") UUID userId);

    @Modifying
    @Query("DELETE FROM BillingPaymentJpaEntity p WHERE p.billingCycleId IN " +
            "(SELECT c.id FROM BillingCycleJpaEntity c WHERE c.creditCardId = :creditCardId)")
    void deleteAllByCreditCardId(@Param("creditCardId") UUID creditCardId);
}
