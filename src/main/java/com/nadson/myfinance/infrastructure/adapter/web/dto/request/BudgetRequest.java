package com.nadson.myfinance.infrastructure.adapter.web.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

public record BudgetRequest(
        UUID userId,
        UUID categoryId,
        int month,
        int year,
        BigDecimal limitAmount
) {}