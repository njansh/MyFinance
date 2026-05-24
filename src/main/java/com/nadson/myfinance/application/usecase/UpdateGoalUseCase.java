package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.UpdateGoalPort;
import com.nadson.myfinance.application.port.out.GoalRepositoryPort;
import com.nadson.myfinance.application.port.out.UserRepositoryPort;
import com.nadson.myfinance.domain.entity.Goal;
import com.nadson.myfinance.domain.exception.BusinessRuleException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class UpdateGoalUseCase implements UpdateGoalPort {
    private final UserRepositoryPort userRepositoryPort;
    private final GoalRepositoryPort goalRepositoryPort;

    public UpdateGoalUseCase(UserRepositoryPort userRepositoryPort, GoalRepositoryPort goalRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
        this.goalRepositoryPort = goalRepositoryPort;
    }

    @Override
    public Goal execute(UUID userId, UUID id, String description, BigDecimal targetAmount, List<UUID> accountIds) {
        if (userRepositoryPort.findById(userId) == null) {
            throw new BusinessRuleException("User not found");
        }

        Goal goal = goalRepositoryPort.findById(id)
                .orElseThrow(() -> new BusinessRuleException("Goal not found"));

        if (!goal.getUserId().equals(userId)) {
            throw new BusinessRuleException("Access denied. This goal does not belong to the user.");
        }


        goal.update(description, targetAmount, accountIds);

        return goalRepositoryPort.save(goal);
    }
}