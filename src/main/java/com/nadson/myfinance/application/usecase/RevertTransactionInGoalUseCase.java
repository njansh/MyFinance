package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.RevertTransactionInGoalPort;
import com.nadson.myfinance.application.port.out.GoalRepositoryPort;
import com.nadson.myfinance.domain.entity.Goal;
import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.domain.enums.TransactionType;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class RevertTransactionInGoalUseCase implements RevertTransactionInGoalPort {

    private final GoalRepositoryPort goalRepositoryPort;

    public RevertTransactionInGoalUseCase(GoalRepositoryPort goalRepositoryPort) {
        this.goalRepositoryPort = goalRepositoryPort;
    }

    @Override
    @Transactional
    public void execute(Transaction transaction) {
        if (transaction == null || transaction.getAccountId() == null) {
            return;
        }

        List<Goal> affectedGoals = goalRepositoryPort.findByAccountId(transaction.getAccountId());
        if (affectedGoals == null || affectedGoals.isEmpty()) {
            return;
        }

        for (Goal goal : affectedGoals) {
            if (transaction.getType() == TransactionType.INCOME) {
                goal.subtractAmount(transaction.getAmount());
            } else if (transaction.getType() == TransactionType.EXPENSE) {
                goal.addAmount(transaction.getAmount());
            }

            goalRepositoryPort.save(goal);
        }
    }
}