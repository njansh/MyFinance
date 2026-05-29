package com.nadson.myfinance.infrastructure.adapter.persistence.entity;

import com.nadson.myfinance.domain.entity.BillingCycle;
import com.nadson.myfinance.domain.enums.BillingCycleStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class BillingCycleJpaEntityTest {

    @Test
    @DisplayName("Deve cobrir 100% da entidade BillingCycleJpaEntity")
    void shouldCoverBillingCycleJpaEntity() {
        // Setup
        UUID id = UUID.randomUUID();
        UUID creditCardId = UUID.randomUUID();
        LocalDate now = LocalDate.now();
        BigDecimal amount = new BigDecimal("100.00");
        BillingCycle domain = new BillingCycle(id, creditCardId, now, now, now, amount, BillingCycleStatus.OPEN);

        // 1. Construtor vazio e Setters
        BillingCycleJpaEntity entity = new BillingCycleJpaEntity();
        entity.setTotalAmount(amount);
        entity.setStatus(BillingCycleStatus.OPEN);

        // 2. Construtor com domínio
        BillingCycleJpaEntity entityFromDomain = new BillingCycleJpaEntity(domain);

        // 3. ToDomain
        BillingCycle result = entityFromDomain.toDomain();

        // Asserts para garantir cobertura dos Getters
        assertEquals(id, entityFromDomain.getId());
        assertEquals(creditCardId, entityFromDomain.getCreditCardId());
        assertEquals(now, entityFromDomain.getStartDate());
        assertEquals(now, entityFromDomain.getClosingDate());
        assertEquals(now, entityFromDomain.getDueDate());
        assertEquals(amount, entityFromDomain.getTotalAmount());
        assertEquals(BillingCycleStatus.OPEN, entityFromDomain.getStatus());
        assertEquals(amount, result.getTotalAmount());
        assertNull(entityFromDomain.getVersion());


    }
}