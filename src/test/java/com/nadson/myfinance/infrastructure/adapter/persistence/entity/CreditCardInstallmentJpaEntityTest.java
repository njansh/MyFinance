package com.nadson.myfinance.infrastructure.adapter.persistence.entity;

import com.nadson.myfinance.domain.entity.CreditCardInstallment;
import com.nadson.myfinance.domain.enums.InstallmentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CreditCardInstallmentJpaEntityTest {

    @Test
    @DisplayName("Deve cobrir 100% da entidade CreditCardInstallmentJpaEntity")
    void shouldCoverCreditCardInstallmentJpaEntity() {
        // Setup
        UUID id = UUID.randomUUID();
        UUID purchaseId = UUID.randomUUID();
        UUID billingCycleId = UUID.randomUUID();
        int number = 1;
        BigDecimal amount = new BigDecimal("50.00");
        InstallmentStatus status = InstallmentStatus.PENDING;

        CreditCardInstallment domain = new CreditCardInstallment(id, purchaseId, billingCycleId, number, amount, status);

        // 1. Testar construtor vazio e setters
        CreditCardInstallmentJpaEntity entity = new CreditCardInstallmentJpaEntity();
        entity.setId(id);
        entity.setPurchaseId(purchaseId);
        entity.setBillingCycleId(billingCycleId);
        entity.setInstallmentNumber(number);
        entity.setAmount(amount);
        entity.setStatus(status);

        // 2. Testar construtor com domínio
        CreditCardInstallmentJpaEntity entityFromDomain = new CreditCardInstallmentJpaEntity(domain);

        // 3. Testar toDomain
        CreditCardInstallment result = entityFromDomain.toDomain();

        // 4. Assertivas para cobrir getters
        assertEquals(id, entity.getId());
        assertEquals(purchaseId, entity.getPurchaseId());
        assertEquals(billingCycleId, entity.getBillingCycleId());
        assertEquals(number, entity.getInstallmentNumber());
        assertEquals(amount, entity.getAmount());
        assertEquals(status, entity.getStatus());

        // Assert do domínio convertido
        assertEquals(id, result.getId());
        assertEquals(amount, result.getAmount());
    }
}