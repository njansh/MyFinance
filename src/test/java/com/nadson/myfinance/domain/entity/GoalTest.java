package com.nadson.myfinance.domain.entity;

import com.nadson.myfinance.domain.exception.BusinessRuleException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GoalTest {

    // --- CENÁRIOS DE SUCESSO ---

    @Test
    @DisplayName("Should create goal successfully with valid data")
    void shouldCreateValidGoal() {
        Goal goal = new Goal(UUID.randomUUID(), UUID.randomUUID(), "Viagem", new BigDecimal("1000.00"), BigDecimal.ZERO, null);
        assertThat(goal.getDescription()).isEqualTo("Viagem");
    }

    @Test
    @DisplayName("Should update goal successfully with valid data")
    void shouldUpdateGoal() {
        Goal goal = new Goal(null, UUID.randomUUID(), "Antiga", new BigDecimal("1000.00"), BigDecimal.ZERO, null);
        goal.update("Nova", new BigDecimal("2000.00"), List.of(UUID.randomUUID()));

        assertThat(goal.getDescription()).isEqualTo("Nova");
        assertThat(goal.getTargetAmount()).isEqualTo(new BigDecimal("2000.00"));
    }

    // --- CENÁRIOS DE ERRO (VALIDAÇÃO) ---

    @Test
    @DisplayName("Should throw exception when creating goal with null or blank description")
    void shouldThrowErrorForInvalidDescription() {
        assertThrows(BusinessRuleException.class, () ->
                new Goal(null, UUID.randomUUID(), "", new BigDecimal("100.00"), null, null));
        assertThrows(BusinessRuleException.class, () ->
                new Goal(null, UUID.randomUUID(), null, new BigDecimal("100.00"), null, null));
    }

    @Test
    @DisplayName("Should throw exception when target amount is zero or negative")
    void shouldThrowErrorForInvalidTargetAmount() {
        assertThrows(BusinessRuleException.class, () ->
                new Goal(null, UUID.randomUUID(), "Teste", BigDecimal.ZERO, null, null));
        assertThrows(BusinessRuleException.class, () ->
                new Goal(null, UUID.randomUUID(), "Teste", new BigDecimal("-50.00"), null, null));
    }

    @Test
    @DisplayName("Should throw exception when adding null or non-positive amount")
    void shouldThrowErrorForInvalidAddAmount() {
        Goal goal = new Goal(null, UUID.randomUUID(), "Teste", new BigDecimal("100.00"), BigDecimal.ZERO, null);

        assertThrows(BusinessRuleException.class, () -> goal.addAmount(null));
        assertThrows(BusinessRuleException.class, () -> goal.addAmount(new BigDecimal("-10.00")));
    }

    @Test
    @DisplayName("Should throw exception when subtracting null or non-positive amount")
    void shouldThrowErrorForInvalidSubtractAmount() {
        Goal goal = new Goal(null, UUID.randomUUID(), "Teste", new BigDecimal("100.00"), new BigDecimal("50.00"), null);

        assertThrows(BusinessRuleException.class, () -> goal.subtractAmount(null));
        assertThrows(BusinessRuleException.class, () -> goal.subtractAmount(new BigDecimal("-5.00")));
    }
}