package com.nadson.myfinance.domain.exception;

import com.nadson.myfinance.domain.enums.AlertType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomDomainExceptionsTest {

    @Test
    @DisplayName("Should cover BudgetAlertException and properties")
    void testBudgetAlertException() {
        String message = "Budget limit exceeded";
        AlertType type = AlertType.EIGHTY_PERCENT;

        BudgetAlertException exception = new BudgetAlertException(message, type);

        assertEquals(message, exception.getMessage());
        assertEquals(type, exception.getAlertType());
    }

    @Test
    @DisplayName("Should cover DuplicateResourceException constructor")
    void testDuplicateResourceException() {
        String message = "Resource already exists in database";
        DuplicateResourceException exception = new DuplicateResourceException(message);

        assertEquals(message, exception.getMessage());
    }
}