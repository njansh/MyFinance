package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.CreateGoalPort;
import com.nadson.myfinance.application.port.out.GoalRepositoryPort;
import com.nadson.myfinance.application.port.out.UserRepositoryPort;
import com.nadson.myfinance.domain.entity.Goal;
import com.nadson.myfinance.domain.exception.BusinessRuleException;

import java.math.BigDecimal;
import java.util.UUID;

public class CreateGoalUseCase implements CreateGoalPort {
    private final GoalRepositoryPort repository;
    private final UserRepositoryPort userRepository;

    public CreateGoalUseCase(GoalRepositoryPort repository, UserRepositoryPort userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Override
    public Goal execute(UUID userId, String description, BigDecimal targetAmount) {
        if (userRepository.findById(userId) == null) {
            throw new BusinessRuleException("User not found");
        }
        Goal goal = new Goal(null, userId, description, targetAmount, BigDecimal.ZERO);
        return repository.save(goal);
    }
}