//package com.nadson.myfinance.domain.entity;
//
//import com.nadson.myfinance.domain.exception.BusinessRuleException;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//
//import java.math.BigDecimal;
//import java.util.UUID;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.*;
//
//class BudgetTest {
//
//    @Test
//    @DisplayName("Should create budget successfully with valid data")
//    void shouldCreateBudgetSuccessfully() {
//        Budget budget = new Budget(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 5, 2026, new BigDecimal("1000.00"));
//
//        assertThat(budget.getLimitAmount()).isEqualTo(new BigDecimal("1000.00"));
//        assertThat(budget.getSpentAmount()).isEqualTo(BigDecimal.ZERO);
//    }
//
//    @Test
//    @DisplayName("Should throw exception when limit is zero or negative")
//    void shouldThrowExceptionWhenLimitIsZeroOrNegative() {
//        assertThrows(BusinessRuleException.class, () ->
//                new Budget(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 5, 2026, BigDecimal.ZERO)
//        );
//        assertThrows(BusinessRuleException.class, () ->
//                new Budget(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 5, 2026, new BigDecimal("-10.00"))
//        );
//    }
//
//    @Test
//    @DisplayName("Should trigger alert when nearing limit (80%)")
//    void shouldTriggerAlertWhenNearingLimit() {
//        Budget budget = new Budget(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 5, 2026, new BigDecimal("1000.00"));
//
//        budget.addExpense(new BigDecimal("800.00")); // Atinge exatamente 80%
//
//        assertTrue(budget.isNearingLimit());
//        assertFalse(budget.isExceeded());
//    }
//
//    @Test
//    @DisplayName("Should trigger alert when limit is exceeded")
//    void shouldTriggerAlertWhenExceeded() {
//        Budget budget = new Budget(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 5, 2026, new BigDecimal("1000.00"));
//
//        budget.addExpense(new BigDecimal("1001.00"));
//
//        assertTrue(budget.isNearingLimit());
//        assertTrue(budget.isExceeded());
//    }
//}