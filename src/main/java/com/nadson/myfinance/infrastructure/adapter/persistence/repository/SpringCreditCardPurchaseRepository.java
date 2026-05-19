package com.nadson.myfinance.infrastructure.adapter.persistence.repository;

import com.nadson.myfinance.infrastructure.adapter.persistence.entity.CreditCardPurchaseJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public interface SpringCreditCardPurchaseRepository extends JpaRepository<CreditCardPurchaseJpaEntity, UUID> {
    List<CreditCardPurchaseJpaEntity> findByCreditCardId(UUID creditCardId);

}