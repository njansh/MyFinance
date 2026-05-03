package com.nadson.myfinance.application.port.in;

import com.nadson.myfinance.domain.entity.Goal;

import java.math.BigDecimal;
import java.util.UUID;

public interface CreateGoalPort {
    Goal execute(UUID userId, String description, BigDecimal targetAmount);
}
