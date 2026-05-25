package com.nadson.myfinance.application.port.in;

import com.nadson.myfinance.domain.entity.Goal;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface UpdateGoalPort {
    Goal execute(UUID userId,UUID id, String description, BigDecimal targetAmount, List<UUID> accountIds);
}