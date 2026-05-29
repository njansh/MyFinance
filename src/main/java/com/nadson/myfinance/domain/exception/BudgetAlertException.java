package com.nadson.myfinance.domain.exception;

import com.nadson.myfinance.domain.enums.AlertType;

public class BudgetAlertException extends RuntimeException {
    private final AlertType alertType;

    public BudgetAlertException(String message, AlertType alertType) {
        super(message);
        this.alertType = alertType;
    }

    public AlertType getAlertType() {
        return alertType;
    }
}