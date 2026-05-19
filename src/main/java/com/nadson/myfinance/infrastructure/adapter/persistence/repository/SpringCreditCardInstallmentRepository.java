package com.nadson.myfinance.infrastructure.adapter.persistence.repository;

import com.nadson.myfinance.infrastructure.adapter.persistence.entity.CreditCardInstallmentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface SpringCreditCardInstallmentRepository extends JpaRepository<CreditCardInstallmentJpaEntity, UUID> {
    List<CreditCardInstallmentJpaEntity> findByBillingCycleId(UUID billingCycleId);
}