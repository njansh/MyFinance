package com.nadson.myfinance.domain.entity;

import com.nadson.myfinance.domain.enums.InstallmentStatus;
import com.nadson.myfinance.domain.exception.BusinessRuleException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreditCardInstallmentTest {

    @Test
    @DisplayName("Deveria criar parcela com sucesso e cobrir métodos de acesso")
    void shouldCreateInstallmentSuccessfully() {
        UUID id = UUID.randomUUID();
        UUID purchaseId = UUID.randomUUID();
        UUID billingCycleId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("50.00");

        CreditCardInstallment installment = new CreditCardInstallment(id, purchaseId, billingCycleId, 1, amount, InstallmentStatus.PENDING);

        // Testa getters e construtor
        assertThat(installment.getId()).isEqualTo(id);
        assertThat(installment.getPurchaseId()).isEqualTo(purchaseId);
        assertThat(installment.getBillingCycleId()).isEqualTo(billingCycleId);
        assertThat(installment.getInstallmentNumber()).isEqualTo(1);
        assertThat(installment.getAmount()).isEqualByComparingTo(amount);
        assertThat(installment.getStatus()).isEqualTo(InstallmentStatus.PENDING);
    }

    @Test
    @DisplayName("Deve disparar exceções de validação no construtor")
    void shouldValidateFields() {
        UUID validId = UUID.randomUUID();

        // purchaseId nulo
        assertThrows(BusinessRuleException.class, () -> new CreditCardInstallment(null, null, validId, 1, BigDecimal.TEN, null));
        // billingCycleId nulo
        assertThrows(BusinessRuleException.class, () -> new CreditCardInstallment(null, validId, null, 1, BigDecimal.TEN, null));
        // Amount negativo
        assertThrows(BusinessRuleException.class, () -> new CreditCardInstallment(null, validId, validId, 1, new BigDecimal("-1"), null));
        // Numero da parcela < 1
        assertThrows(BusinessRuleException.class, () -> new CreditCardInstallment(null, validId, validId, 0, BigDecimal.TEN, null));
    }

    @Test
    @DisplayName("Deve cobrir setAmount, setStatus e markAsPaid")
    void shouldCoverSettersAndMethods() {
        CreditCardInstallment installment = new CreditCardInstallment(null, UUID.randomUUID(), UUID.randomUUID(), 1, BigDecimal.TEN, null);

        // Testa markAsPaid
        installment.markAsPaid();
        assertThat(installment.getStatus()).isEqualTo(InstallmentStatus.PAID);

        // Testa setStatus
        installment.setStatus(InstallmentStatus.PENDING);
        assertThat(installment.getStatus()).isEqualTo(InstallmentStatus.PENDING);

        // Testa setAmount (sucesso e erro)
        installment.setAmount(new BigDecimal("20.00"));
        assertThat(installment.getAmount()).isEqualByComparingTo("20.00");
        assertThrows(BusinessRuleException.class, () -> installment.setAmount(new BigDecimal("-5.00")));
    }
}