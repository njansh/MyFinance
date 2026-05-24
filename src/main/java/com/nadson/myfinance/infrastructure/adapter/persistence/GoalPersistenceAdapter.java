package com.nadson.myfinance.infrastructure.adapter.persistence;

import com.nadson.myfinance.application.port.out.GoalRepositoryPort;
import com.nadson.myfinance.domain.entity.Goal;
import com.nadson.myfinance.infrastructure.adapter.persistence.entity.GoalJpaEntity;
import com.nadson.myfinance.infrastructure.adapter.persistence.repository.SpringGoalRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class GoalPersistenceAdapter implements GoalRepositoryPort {

    private final SpringGoalRepository repository;

    public GoalPersistenceAdapter(SpringGoalRepository repository) {
        this.repository = repository;
    }

    @Override
    public Goal save(Goal goal) {
        return repository.save(new GoalJpaEntity(goal)).toDomain();
    }

    @Override
    public Optional<Goal> findById(UUID id) {
        return repository.findById(id).map(GoalJpaEntity::toDomain);
    }

    @Override
    public List<Goal> findByUserId(UUID userId) {
        return repository.findByUserId(userId).stream()
                .map(GoalJpaEntity::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteAllByUserId(UUID userId) {
        repository.deleteAllByUserId(userId);
    }
}