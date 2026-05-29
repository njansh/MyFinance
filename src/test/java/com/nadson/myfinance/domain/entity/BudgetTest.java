package com.nadson.myfinance.domain.entity;

import com.nadson.myfinance.domain.exception.BusinessRuleException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class BudgetTest {

    @Test
    @DisplayName("Should cover 100% of Getters and Constructors")
    void shouldCoverGettersAndConstructors() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        // Construtor 1
        Budget budget1 = new Budget(id, userId, categoryId, 5, 2026, new BigDecimal("1000.00"));
        // Construtor 2 com nulos para acionar ternários
        Budget budget2 = new Budget(null, userId, categoryId, 5, 2026, new BigDecimal("1000.00"), null, false, false);

        assertThat(budget1.getId()).isEqualTo(id);
        assertThat(budget1.getUserId()).isEqualTo(userId);
        assertThat(budget1.getCategoryId()).isEqualTo(categoryId);
        assertThat(budget1.getMonth()).isEqualTo(5);
        assertThat(budget1.getYear()).isEqualTo(2026);
        assertThat(budget1.getLimitAmount()).isEqualByComparingTo("1000.00");
        assertThat(budget1.getSpentAmount()).isEqualTo(BigDecimal.ZERO);
        assertFalse(budget1.isAlertedEightyPercent());
        assertFalse(budget1.isAlertedOneHundredPercent());

        assertThat(budget2.getId()).isNotNull();
        assertThat(budget2.getSpentAmount()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should cover all validation branches in validate()")
    void shouldCoverValidations() {
        UUID uId = UUID.randomUUID();
        UUID cId = UUID.randomUUID();

        // Dispara TODAS as exceções possíveis para deixar validate() 100% verde
        assertThrows(BusinessRuleException.class, () -> new Budget(null, null, cId, 5, 2026, BigDecimal.TEN));
        assertThrows(BusinessRuleException.class, () -> new Budget(null, uId, null, 5, 2026, BigDecimal.TEN));
        assertThrows(BusinessRuleException.class, () -> new Budget(null, uId, cId, 0, 2026, BigDecimal.TEN));
        assertThrows(BusinessRuleException.class, () -> new Budget(null, uId, cId, 13, 2026, BigDecimal.TEN));
        assertThrows(BusinessRuleException.class, () -> new Budget(null, uId, cId, 5, 1999, BigDecimal.TEN));
        assertThrows(BusinessRuleException.class, () -> new Budget(null, uId, cId, 5, 2026, null));
        assertThrows(BusinessRuleException.class, () -> new Budget(null, uId, cId, 5, 2026, new BigDecimal("-1.00")));
    }

    @Test
    @DisplayName("Should cover isExceeded and removeExpense clamping")
    void shouldCoverIsExceededAndRemoveExpense() {
        Budget budget = new Budget(null, UUID.randomUUID(), UUID.randomUUID(), 5, 2026, new BigDecimal("100.00"));

        budget.addExpense(new BigDecimal("101.00"));
        assertTrue(budget.isExceeded());

        budget.removeExpense(new BigDecimal("101.00"));
        assertFalse(budget.isExceeded());

        budget.addExpense(new BigDecimal("10.00"));
        budget.removeExpense(new BigDecimal("50.00"));
        assertThat(budget.getSpentAmount()).isEqualTo(BigDecimal.ZERO);

        budget.addExpense(null);
        budget.removeExpense(null);
    }

    @Test
    @DisplayName("Should cover updateLimit logic branches")
    void shouldCoverUpdateLimit() {
        Budget budget = new Budget(null, UUID.randomUUID(), UUID.randomUUID(), 5, 2026, new BigDecimal("100.00"));
        budget.addExpense(new BigDecimal("90.00"));

        budget.updateLimit(new BigDecimal("50.00"));
        assertTrue(budget.isAlertedOneHundredPercent());
        assertTrue(budget.isAlertedEightyPercent());

        budget.updateLimit(new BigDecimal("110.00"));
        assertTrue(budget.isAlertedEightyPercent());
        assertFalse(budget.isAlertedOneHundredPercent());

        budget.updateLimit(new BigDecimal("200.00"));
        assertFalse(budget.isAlertedEightyPercent());
        assertFalse(budget.isAlertedOneHundredPercent());
    }

    @Test
    @DisplayName("Should cover shouldAlertEightyPercent logic")
    void shouldCoverShouldAlertEightyPercent() {
        Budget budget = new Budget(null, UUID.randomUUID(), UUID.randomUUID(), 5, 2026, new BigDecimal("100.00"));

        assertFalse(budget.shouldAlertEightyPercent()); // Menor que 80% (falso)

        budget.addExpense(new BigDecimal("80.00"));
        assertTrue(budget.shouldAlertEightyPercent()); // Atingiu 80% (verdadeiro)
        assertFalse(budget.shouldAlertEightyPercent()); // Já foi alertado (if de bloqueio)
    }

    @Test
    @DisplayName("Should cover shouldAlertOneHundredPercent logic")
    void shouldCoverShouldAlertOneHundredPercent() {
        Budget budget = new Budget(null, UUID.randomUUID(), UUID.randomUUID(), 5, 2026, new BigDecimal("100.00"));

        assertFalse(budget.shouldAlertOneHundredPercent()); // Menor que 100% (falso)

        budget.addExpense(new BigDecimal("100.00"));
        assertTrue(budget.shouldAlertOneHundredPercent()); // Atingiu 100% (verdadeiro)
        assertFalse(budget.shouldAlertOneHundredPercent()); // Já foi alertado (if de bloqueio)
    }

@Test
@DisplayName("Should cover getUsagePercentage with zero limit to reach 100%")
void shouldCoverUsagePercentageWithZero() {
    // Cria orçamento com limite ZERO
    Budget budget = new Budget(null, UUID.randomUUID(), UUID.randomUUID(), 5, 2026, BigDecimal.ZERO);

    // Isso vai forçar a execução da linha "if (limitAmount.compareTo(BigDecimal.ZERO) == 0)"
    BigDecimal percentage = budget.getUsagePercentage();

    assertThat(percentage).isEqualByComparingTo(BigDecimal.ZERO);
}}