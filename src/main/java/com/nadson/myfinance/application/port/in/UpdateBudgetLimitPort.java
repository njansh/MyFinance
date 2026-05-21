package com.nadson.myfinance.application.port.in;

import com.nadson.myfinance.domain.entity.Budget;
import java.math.BigDecimal;
import java.util.UUID;

public interface UpdateBudgetLimitPort {
    Budget execute(UUID budgetId, BigDecimal newLimit);
}
