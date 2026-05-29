package com.nadson.myfinance.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BillingPaymentTest {

    @Test
    @DisplayName("Deveria criar um record BillingPayment com sucesso")
    void shouldCreateBillingPayment() {
        UUID id = UUID.randomUUID();
        UUID billingCycleId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("100.00");
        LocalDateTime now = LocalDateTime.now();

        BillingPayment payment = new BillingPayment(id, billingCycleId, accountId, amount, now);

        // Acessores de records não possuem prefixo "get"
        assertThat(payment.id()).isEqualTo(id);
        assertThat(payment.billingCycleId()).isEqualTo(billingCycleId);
        assertThat(payment.accountId()).isEqualTo(accountId);
        assertThat(payment.amount()).isEqualByComparingTo(amount);
        assertThat(payment.paymentDate()).isEqualTo(now);
    }
}