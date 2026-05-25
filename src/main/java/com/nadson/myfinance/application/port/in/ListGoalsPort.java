package com.nadson.myfinance.application.port.in;

import com.nadson.myfinance.domain.entity.Goal;
import java.util.List;
import java.util.UUID;

public interface ListGoalsPort {
    List<Goal> execute(UUID userId);
}