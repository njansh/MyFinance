package com.nadson.myfinance.application.port.in;

import com.nadson.myfinance.domain.entity.Budget;

public interface GetBudgetPort {
    Budget execute(java.util.UUID id);
}
