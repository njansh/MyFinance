package com.nadson.myfinance.application.port.in;

import com.nadson.myfinance.domain.entity.Budget;

import java.math.BigDecimal;
import java.util.UUID;

public interface CreateBudgetPort {
    Budget execute(UUID userId, UUID categoryId, int month, int year, BigDecimal limitAmount);
}
