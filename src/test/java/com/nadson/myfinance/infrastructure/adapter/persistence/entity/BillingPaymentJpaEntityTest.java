package com.nadson.myfinance.infrastructure.adapter.persistence.entity;

import com.nadson.myfinance.domain.entity.BillingPayment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BillingPaymentJpaEntityTest {

    @Test
    @DisplayName("Deve cobrir 100% da entidade BillingPaymentJpaEntity")
    void shouldCoverBillingPaymentJpaEntity() {
        // Setup
        UUID id = UUID.randomUUID();
        UUID billingCycleId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("150.00");
        LocalDateTime now = LocalDateTime.now();

        BillingPayment domain = new BillingPayment(id, billingCycleId, accountId, amount, now);

        // 1. Testar construtor vazio
        BillingPaymentJpaEntity entity = new BillingPaymentJpaEntity();

        // 2. Testar construtor com argumentos
        BillingPaymentJpaEntity entityFull = new BillingPaymentJpaEntity(id, billingCycleId, accountId, amount, now);

        // 3. Testar fromDomain e toDomain
        BillingPaymentJpaEntity entityFromDomain = BillingPaymentJpaEntity.fromDomain(domain);
        BillingPayment resultDomain = entityFromDomain.toDomain();

        // 4. Assertivas para cobrir getters
        assertEquals(id, entityFull.getId());
        assertEquals(billingCycleId, entityFull.getBillingCycleId());
        assertEquals(accountId, entityFull.getAccountId());
        assertEquals(amount, entityFull.getAmount());
        assertEquals(now, entityFull.getPaymentDate());

        // Assertivas de conversão
        assertEquals(id, resultDomain.id());
        assertEquals(billingCycleId, resultDomain.billingCycleId());
        assertEquals(accountId, resultDomain.accountId());
        assertEquals(amount, resultDomain.amount());
        assertEquals(now, resultDomain.paymentDate());
    }
}