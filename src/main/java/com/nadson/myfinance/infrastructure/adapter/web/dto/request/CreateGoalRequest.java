package com.nadson.myfinance.infrastructure.adapter.web.dto.request;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CreateGoalRequest(
        String description,
        BigDecimal targetAmount,
        List<UUID> accountIds
) {}