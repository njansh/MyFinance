package com.nadson.myfinance.application.port.in;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public interface GetExpensesByCategoryPort {
    Map<String, BigDecimal> execute(UUID userId);
}