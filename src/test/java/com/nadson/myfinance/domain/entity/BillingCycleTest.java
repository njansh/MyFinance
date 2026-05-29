package com.nadson.myfinance.domain.entity;

import com.nadson.myfinance.domain.enums.BillingCycleStatus;
import com.nadson.myfinance.domain.exception.BusinessRuleException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class BillingCycleTest {

    private final UUID VALID_ID = UUID.randomUUID();
    private final UUID VALID_CARD_ID = UUID.randomUUID();
    private final LocalDate VALID_START = LocalDate.of(2026, 1, 1);
    private final LocalDate VALID_CLOSING = LocalDate.of(2026, 1, 20);
    private final LocalDate VALID_DUE = LocalDate.of(2026, 1, 30);
    private final BigDecimal VALID_AMOUNT = BigDecimal.TEN;
    private final BillingCycleStatus VALID_STATUS = BillingCycleStatus.OPEN;

    @Test
    @DisplayName("Deveria criar ciclo de faturamento com sucesso e cobrir getters")
    void shouldCreateBillingCycle() {
        BillingCycle cycle = new BillingCycle(VALID_ID, VALID_CARD_ID, VALID_START, VALID_CLOSING, VALID_DUE, VALID_AMOUNT, VALID_STATUS);

        assertThat(cycle.getId()).isEqualTo(VALID_ID);
        assertThat(cycle.getCreditCardId()).isEqualTo(VALID_CARD_ID);
        assertThat(cycle.getStartDate()).isEqualTo(VALID_START);
        assertThat(cycle.getClosingDate()).isEqualTo(VALID_CLOSING);
        assertThat(cycle.getDueDate()).isEqualTo(VALID_DUE);
        assertThat(cycle.getTotalAmount()).isEqualByComparingTo("10.00");
        assertThat(cycle.getStatus()).isEqualTo(BillingCycleStatus.OPEN);
    }

    @Test
    @DisplayName("Deve disparar exceções de validação para campos obrigatórios nulos ou valores inválidos")
    void shouldCoverNullAndValueValidateBranches() {
        LocalDate now = LocalDate.now();
        UUID cardId = UUID.randomUUID();

        // Testa IDs nulos que o construtor NÃO corrige automaticamente
        assertThrows(BusinessRuleException.class, () -> new BillingCycle(VALID_ID, null, now, now.plusDays(1), now.plusDays(2), VALID_AMOUNT, VALID_STATUS));
        assertThrows(BusinessRuleException.class, () -> new BillingCycle(VALID_ID, cardId, null, now.plusDays(1), now.plusDays(2), VALID_AMOUNT, VALID_STATUS));
        assertThrows(BusinessRuleException.class, () -> new BillingCycle(VALID_ID, cardId, now, null, now.plusDays(2), VALID_AMOUNT, VALID_STATUS));
        assertThrows(BusinessRuleException.class, () -> new BillingCycle(VALID_ID, cardId, now, now.plusDays(1), null, VALID_AMOUNT, VALID_STATUS));

        // Testa valor negativo (isso o validate pega!)
        assertThrows(BusinessRuleException.class, () -> new BillingCycle(VALID_ID, cardId, now, now.plusDays(1), now.plusDays(2), new BigDecimal("-1"), VALID_STATUS));
    }

    @Test
    @DisplayName("Deve disparar exceções de validação temporal (Datas)")
    void shouldCoverDateValidateBranches() {
        // closingDate antes de startDate (Fechamento dia 1, Início dia 2)
        assertThrows(BusinessRuleException.class, () ->
                new BillingCycle(VALID_ID, VALID_CARD_ID, LocalDate.of(2026, 1, 2), LocalDate.of(2026, 1, 1), VALID_DUE, VALID_AMOUNT, VALID_STATUS));

        // dueDate antes de closingDate (Vencimento dia 15, Fechamento dia 20)
        assertThrows(BusinessRuleException.class, () ->
                new BillingCycle(VALID_ID, VALID_CARD_ID, VALID_START, LocalDate.of(2026, 1, 20), LocalDate.of(2026, 1, 15), VALID_AMOUNT, VALID_STATUS));
    }

    @Test
    @DisplayName("Deveria gerenciar status e pagamentos")
    void shouldManageStatusAndPayments() {
        BillingCycle cycle = new BillingCycle(VALID_ID, VALID_CARD_ID, VALID_START, VALID_CLOSING, VALID_DUE, VALID_AMOUNT, VALID_STATUS);

        cycle.closeCycle();
        assertThat(cycle.getStatus()).isEqualTo(BillingCycleStatus.CLOSED);

        cycle.markAsPaid();
        assertThat(cycle.getStatus()).isEqualTo(BillingCycleStatus.PAID);

        cycle.registerPayment(new BigDecimal("5.00"));
        assertThat(cycle.getTotalAmount()).isEqualByComparingTo("5.00");

        cycle.registerPayment(new BigDecimal("10.00"));
        assertThat(cycle.getTotalAmount()).isEqualByComparingTo("0.00");
    }

    @Test
    @DisplayName("Deve validar a adição de parcelas e cobrir o método addInstallment")
    void shouldCoverAddInstallment() {
        BillingCycle cycle = new BillingCycle(VALID_ID, VALID_CARD_ID, VALID_START, VALID_CLOSING, VALID_DUE, VALID_AMOUNT, VALID_STATUS);

        // 1. Testa erro: parcela nula
        assertThrows(BusinessRuleException.class, () -> cycle.addInstallment(null));

        // Criando um mock do CreditCardInstallment para evitar erros de compilação no construtor
        CreditCardInstallment mockInstallment = Mockito.mock(CreditCardInstallment.class);
        Mockito.when(mockInstallment.getAmount()).thenReturn(new BigDecimal("50.00"));

        // 2. Testa erro: ciclo fechado
        cycle.closeCycle();
        assertThrows(BusinessRuleException.class, () -> cycle.addInstallment(mockInstallment));

        // 3. Testa sucesso: adicionando parcela em ciclo aberto
        BillingCycle openCycle = new BillingCycle(VALID_ID, VALID_CARD_ID, VALID_START, VALID_CLOSING, VALID_DUE, BigDecimal.ZERO, BillingCycleStatus.OPEN);
        openCycle.addInstallment(mockInstallment);
        assertThat(openCycle.getTotalAmount()).isEqualByComparingTo("50.00");
    }
    @Test
    @DisplayName("Cobre casos de nulos no construtor para atingir 100% de cobertura")
    void shouldCoverConstructorNullBranches() {
        // Testa caso onde o ID é nulo (força a linha 'this.id = id == null ? ...')
        BillingCycle cycle = new BillingCycle(null, VALID_CARD_ID, VALID_START, VALID_CLOSING, VALID_DUE, VALID_AMOUNT, VALID_STATUS);
        assertThat(cycle.getId()).isNotNull();

        // Testa caso onde o totalAmount é nulo (força a linha 'this.totalAmount = totalAmount == null ? ...')
        BillingCycle cycleNullAmount = new BillingCycle(VALID_ID, VALID_CARD_ID, VALID_START, VALID_CLOSING, VALID_DUE, null, VALID_STATUS);
        assertThat(cycleNullAmount.getTotalAmount()).isEqualByComparingTo("0.00");
    }
}