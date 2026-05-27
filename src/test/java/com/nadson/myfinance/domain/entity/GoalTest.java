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

    // --- CENÁRIOS DE SUCESSO E ESTADO ---

    @Test
    @DisplayName("Should increment current amount correctly")
    void shouldAddAmountSuccessfully() {
        Goal goal = new Goal(null, UUID.randomUUID(), "Economia", new BigDecimal("1000.00"), new BigDecimal("100.00"), null);
        goal.addAmount(new BigDecimal("50.00"));

        assertThat(goal.getCurrentAmount()).isEqualByComparingTo("150.00");
    }

    @Test
    @DisplayName("Should decrement current amount correctly")
    void shouldSubtractAmountSuccessfully() {
        Goal goal = new Goal(null, UUID.randomUUID(), "Economia", new BigDecimal("1000.00"), new BigDecimal("100.00"), null);
        goal.subtractAmount(new BigDecimal("40.00"));

        assertThat(goal.getCurrentAmount()).isEqualByComparingTo("60.00");
    }

    @Test
    @DisplayName("Should set current amount to zero if subtraction exceeds balance")
    void shouldNotAllowNegativeCurrentAmount() {
        Goal goal = new Goal(null, UUID.randomUUID(), "Economia", new BigDecimal("1000.00"), new BigDecimal("10.00"), null);
        goal.subtractAmount(new BigDecimal("50.00")); // Tentando subtrair mais do que tem

        assertThat(goal.getCurrentAmount()).isEqualByComparingTo("0");
    }

    @Test
    @DisplayName("Should successfully update fields via update method")
    void shouldUpdateGoalFields() {
        Goal goal = new Goal(null, UUID.randomUUID(), "Antiga", new BigDecimal("1000.00"), null, null);
        List<UUID> newAccounts = List.of(UUID.randomUUID());

        goal.update("Nova", new BigDecimal("5000.00"), newAccounts);

        assertThat(goal.getDescription()).isEqualTo("Nova");
        assertThat(goal.getTargetAmount()).isEqualByComparingTo("5000.00");
        assertThat(goal.getAccountIds()).hasSize(1);
    }

    // --- CENÁRIOS DE ERRO ---

    @Test
    @DisplayName("Should throw exception when creating goal with null UserID")
    void shouldThrowErrorForNullUserId() {
        assertThrows(BusinessRuleException.class, () ->
                new Goal(null, null, "Teste", new BigDecimal("100.00"), null, null));
    }

    @Test
    @DisplayName("Should throw exception for blank description in update")
    void shouldThrowErrorForBlankUpdateDescription() {
        Goal goal = new Goal(null, UUID.randomUUID(), "Valid", new BigDecimal("100.00"), null, null);
        assertThrows(BusinessRuleException.class, () -> goal.update(" ", null, null));
    }
}