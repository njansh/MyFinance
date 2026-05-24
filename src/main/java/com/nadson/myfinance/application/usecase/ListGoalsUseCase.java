package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.ListGoalsPort;
import com.nadson.myfinance.application.port.out.GoalRepositoryPort;
import com.nadson.myfinance.application.port.out.UserRepositoryPort;
import com.nadson.myfinance.domain.entity.Goal;
import com.nadson.myfinance.domain.exception.BusinessRuleException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ListGoalsUseCase implements ListGoalsPort {

    private final GoalRepositoryPort repository;
    private final UserRepositoryPort userRepositoryPort;

    public ListGoalsUseCase(GoalRepositoryPort repository, UserRepositoryPort userRepositoryPort) {
        this.repository = repository;
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public List<Goal> execute(UUID userId) {
        if (userRepositoryPort.findById(userId) == null) {
            throw new BusinessRuleException("User not found");
        }




        return repository.findByUserId(userId);
    }
}