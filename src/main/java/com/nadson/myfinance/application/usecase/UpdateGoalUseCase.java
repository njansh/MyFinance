package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.UpdateGoalPort;
import com.nadson.myfinance.application.port.out.AccountRepositoryPort; // Injetar este
import com.nadson.myfinance.application.port.out.GoalRepositoryPort;
import com.nadson.myfinance.application.port.out.UserRepositoryPort;
import com.nadson.myfinance.domain.entity.Account;
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
    private final AccountRepositoryPort accountRepositoryPort;

    public UpdateGoalUseCase(UserRepositoryPort userRepositoryPort,
                             GoalRepositoryPort goalRepositoryPort,
                             AccountRepositoryPort accountRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
        this.goalRepositoryPort = goalRepositoryPort;
        this.accountRepositoryPort = accountRepositoryPort;
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

        if (accountIds != null) {
            for (UUID accountId : accountIds) {
                Account account = accountRepositoryPort.findById(accountId);
                if (account == null) {
                    throw new BusinessRuleException("Account not found: " + accountId);
                }
                if (!account.getUserId().equals(userId)) {
                    throw new BusinessRuleException("Access denied. Account does not belong to the user: " + accountId);
                }
            }
        }

        goal.update(description, targetAmount, accountIds);

        return goalRepositoryPort.save(goal);
    }
}