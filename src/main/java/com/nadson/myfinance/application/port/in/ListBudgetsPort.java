package com.nadson.myfinance.application.port.in;

import com.nadson.myfinance.domain.entity.Budget;
import java.util.List;
import java.util.UUID;

public interface ListBudgetsPort {
    List<Budget> execute(UUID userId, int month, int year);
}
