package com.nadson.myfinance.application.port.out;

import com.nadson.myfinance.domain.entity.Goal;

import java.util.List;
import java.util.UUID;

public interface GoalRepositoryPort {
    Goal save(Goal goal);
    List<Goal> findByUserId(UUID userId);

    void deleteAllByUserId(UUID userId);
}
