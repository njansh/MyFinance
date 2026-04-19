package com.nadson.myfinance.infrastructure.adapter.web.dto.response;

import java.math.BigDecimal;

public record BalanceResponse(
        BigDecimal totalIncomes,
        BigDecimal totalExpenses,
        BigDecimal balance
) {}
