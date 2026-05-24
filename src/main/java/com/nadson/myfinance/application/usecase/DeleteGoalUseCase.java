package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.DeleteGoalPort;
import com.nadson.myfinance.application.port.out.GoalRepositoryPort;
import com.nadson.myfinance.application.port.out.UserRepositoryPort;
import com.nadson.myfinance.domain.entity.Goal;
import com.nadson.myfinance.domain.exception.BusinessRuleException;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class DeleteGoalUseCase implements DeleteGoalPort {

    private final GoalRepositoryPort repository;
    private final UserRepositoryPort userRepositoryPort;
    public DeleteGoalUseCase(GoalRepositoryPort repository, UserRepositoryPort userRepositoryPort) {
        this.repository = repository;
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public void execute(UUID id ,UUID userId) {
        if (userRepositoryPort.findById(userId) == null) {
            throw new BusinessRuleException("User not found");
        }

        Goal goal = repository.findById(id)
                .orElseThrow(() -> new BusinessRuleException("Goal not found"));

        if (!goal.getUserId().equals(userId)) {
            throw new BusinessRuleException("Access denied. This goal does not belong to the user.");
        }


        repository.deleteById(id);
    }
}