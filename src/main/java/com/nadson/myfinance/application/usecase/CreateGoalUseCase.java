package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.CreateGoalPort;
import com.nadson.myfinance.application.port.out.AccountRepositoryPort; // 1. Injetamos o Account
import com.nadson.myfinance.application.port.out.GoalRepositoryPort;
import com.nadson.myfinance.application.port.out.UserRepositoryPort;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.entity.Goal;
import com.nadson.myfinance.domain.exception.BusinessRuleException;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class CreateGoalUseCase implements CreateGoalPort {
    private final GoalRepositoryPort repository;
    private final UserRepositoryPort userRepository;
    private final AccountRepositoryPort accountRepository; // 2. Adicionado

    public CreateGoalUseCase(GoalRepositoryPort repository, UserRepositoryPort userRepository, AccountRepositoryPort accountRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public Goal execute(UUID userId, String description, BigDecimal targetAmount, List<UUID> accountIds) {
        if (userRepository.findById(userId) == null) {
            throw new BusinessRuleException("User not found");
        }

        BigDecimal initialBalance = BigDecimal.ZERO;
        if (accountIds != null) {
            for (UUID accountId : accountIds) {
                Account account = accountRepository.findById(accountId);
                if (account != null) {
                    initialBalance = initialBalance.add(account.getBalance());
                }
            }
        }

        Goal goal = new Goal(null, userId, description, targetAmount, initialBalance, accountIds);
        return repository.save(goal);
    }
}