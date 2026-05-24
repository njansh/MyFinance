package com.nadson.myfinance.application.port.out;

import com.nadson.myfinance.domain.entity.Goal;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GoalRepositoryPort {
    Goal save(Goal goal);
    List<Goal> findByUserId(UUID userId);
    Optional<Goal> findById(UUID id);
    void deleteById(UUID id);
    void deleteAllByUserId(UUID userId);
}
