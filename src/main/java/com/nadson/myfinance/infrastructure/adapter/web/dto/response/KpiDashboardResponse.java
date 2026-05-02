package com.nadson.myfinance.infrastructure.adapter.web.dto.response;

import java.math.BigDecimal;

public record KpiDashboardResponse(
        BigDecimal netWorth,
        BigDecimal monthlyIncome,
        BigDecimal monthlyExpense,
        BigDecimal cashFlow,
        BigDecimal savingsRatio
) {}