package com.nadson.myfinance.domain.entity;

import com.nadson.myfinance.domain.exception.BusinessRuleException;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

    class BudgetTest {

    @Test
    void shouldCreateBudgetSuccessfully() {
        Budget budget = new Budget(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 5, 2026, new BigDecimal("1000.00"));

        assertEquals(new BigDecimal("1000.00"), budget.getLimitAmount());
        assertEquals(BigDecimal.ZERO, budget.getSpentAmount());
    }

    @Test
    void shouldThrowExceptionWhenLimitIsZeroOrNegative() {
        assertThrows(BusinessRuleException.class, () ->
                new Budget(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 5, 2026, BigDecimal.ZERO)
        );
    }

    @Test
    void shouldTriggerAlertWhenNearingLimit() {
        Budget budget = new Budget(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 5, 2026, new BigDecimal("1000.00"));

        budget.addExpense(new BigDecimal("800.00")); // Atinge exatamente 80%

        assertTrue(budget.isNearingLimit());
        assertFalse(budget.isExceeded());
    }

    @Test
    void shouldTriggerAlertWhenExceeded() {
        Budget budget = new Budget(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 5, 2026, new BigDecimal("1000.00"));

        budget.addExpense(new BigDecimal("1001.00"));

        assertTrue(budget.isNearingLimit());
        assertTrue(budget.isExceeded());
    }
}