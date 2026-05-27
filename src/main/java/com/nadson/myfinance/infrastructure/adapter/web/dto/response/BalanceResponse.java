package com.nadson.myfinance.infrastructure.adapter.web.dto.response;

import java.math.BigDecimal;

public record BalanceResponse(
        BigDecimal incomes,
        BigDecimal expenses,
        BigDecimal balance
) {
    public BigDecimal getIncomes() {
        return incomes;
    }

    public BigDecimal getExpenses() {
        return expenses;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}
