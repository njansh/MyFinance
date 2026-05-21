package com.nadson.myfinance.infrastructure.adapter.web.dto.response;

import com.nadson.myfinance.domain.entity.Budget;
import java.math.BigDecimal;
import java.util.UUID;

public record BudgetResponse(
        UUID id,
        UUID userId,
        UUID categoryId,
        int month,
        int year,
        BigDecimal limitAmount,
        BigDecimal spentAmount,
        BigDecimal usagePercentage
) {

        public BudgetResponse(Budget budget) {
        this(
                budget.getId(),
                budget.getUserId(),
                budget.getCategoryId(),
                budget.getMonth(),
                budget.getYear(),
                budget.getLimitAmount(),
                budget.getSpentAmount(),
                budget.getUsagePercentage()
        );
    }
}