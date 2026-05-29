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

    @Test
    @DisplayName("Should create goal successfully with valid data and nulls treated")
    void shouldCreateGoalSuccessfully() {
        // Testa o construtor com valores nulos (para cobrir os operadores ternários)
        Goal goal = new Goal(null, UUID.randomUUID(), "Viagem", new BigDecimal("1000.00"), null, null);

        assertThat(goal.getId()).isNotNull();
        assertThat(goal.getCurrentAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(goal.getAccountIds()).isEmpty();
    }

    @Test
    @DisplayName("Should throw BusinessRuleException when validation fails in constructor")
    void shouldThrowExceptionOnInvalidConstructor() {
        UUID userId = UUID.randomUUID();

        // Testa validação do UserId
        assertThrows(BusinessRuleException.class, () -> new Goal(null, null, "Desc", BigDecimal.TEN, null, null));

        // Testa validação da Description (null ou blank)
        assertThrows(BusinessRuleException.class, () -> new Goal(null, userId, "", BigDecimal.TEN, null, null));

        // Testa validação do TargetAmount (<= 0)
        assertThrows(BusinessRuleException.class, () -> new Goal(null, userId, "Desc", BigDecimal.ZERO, null, null));
    }

    @Test
    @DisplayName("Should update goal fields correctly")
    void shouldUpdateGoal() {
        Goal goal = new Goal(UUID.randomUUID(), UUID.randomUUID(), "Old", new BigDecimal("100.00"), null, null);

        // Testa atualização válida
        goal.update("New", new BigDecimal("200.00"), List.of(UUID.randomUUID()));
        assertThat(goal.getDescription()).isEqualTo("New");
        assertThat(goal.getTargetAmount()).isEqualByComparingTo("200.00");

        // Testa atualização com description em branco (deve lançar exceção)
        assertThrows(BusinessRuleException.class, () -> goal.update(" ", null, null));

        // Testa atualização com targetAmount inválido (deve lançar exceção)
        assertThrows(BusinessRuleException.class, () -> goal.update(null, new BigDecimal("-10.00"), null));
    }

    @Test
    @DisplayName("Should add amount correctly")
    void shouldAddAmount() {
        Goal goal = new Goal(UUID.randomUUID(), UUID.randomUUID(), "Meta", new BigDecimal("100.00"), BigDecimal.ZERO, null);
        goal.addAmount(new BigDecimal("50.00"));
        assertThat(goal.getCurrentAmount()).isEqualByComparingTo("50.00");

        // Testa exceções de addAmount
        assertThrows(BusinessRuleException.class, () -> goal.addAmount(null));
        assertThrows(BusinessRuleException.class, () -> goal.addAmount(BigDecimal.ZERO));
    }

    @Test
    @DisplayName("Should subtract amount and clamp to zero if negative")
    void shouldSubtractAmount() {
        Goal goal = new Goal(UUID.randomUUID(), UUID.randomUUID(), "Meta", new BigDecimal("100.00"), new BigDecimal("50.00"), null);

        // Subtração normal
        goal.subtractAmount(new BigDecimal("20.00"));
        assertThat(goal.getCurrentAmount()).isEqualByComparingTo("30.00");

        // Subtração que força saldo negativo (deve resetar para zero)
        goal.subtractAmount(new BigDecimal("100.00"));
        assertThat(goal.getCurrentAmount()).isEqualByComparingTo("0.00");

        // Testa exceções de subtractAmount
        assertThrows(BusinessRuleException.class, () -> goal.subtractAmount(null));
        assertThrows(BusinessRuleException.class, () -> goal.subtractAmount(new BigDecimal("-10")));
    }

    @Test
    @DisplayName("Should verify getters coverage")
    void shouldVerifyGetters() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Goal goal = new Goal(id, userId, "Test", BigDecimal.TEN, BigDecimal.ONE, List.of());

        assertThat(goal.getId()).isEqualTo(id);
        assertThat(goal.getUserId()).isEqualTo(userId);
        assertThat(goal.getDescription()).isEqualTo("Test");
        assertThat(goal.getTargetAmount()).isEqualTo(BigDecimal.TEN);
        assertThat(goal.getCurrentAmount()).isEqualTo(BigDecimal.ONE);
    }
}