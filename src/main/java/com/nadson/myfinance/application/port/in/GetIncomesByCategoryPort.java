package com.nadson.myfinance.application.port.in;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public interface GetIncomesByCategoryPort {
    Map<String, BigDecimal> execute(UUID accountId, LocalDateTime startDate, LocalDateTime endDate);
}

