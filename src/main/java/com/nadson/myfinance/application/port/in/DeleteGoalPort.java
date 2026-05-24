package com.nadson.myfinance.application.port.in;

import java.util.UUID;

public interface DeleteGoalPort {
    void execute(UUID goalId, UUID userId);
}
