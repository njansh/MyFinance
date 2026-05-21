package com.nadson.myfinance.infrastructure.adapter.persistence;

import com.nadson.myfinance.application.port.out.BudgetRepositoryPort;
import com.nadson.myfinance.domain.entity.Budget;
import com.nadson.myfinance.infrastructure.adapter.persistence.entity.BudgetJpaEntity;
import com.nadson.myfinance.infrastructure.adapter.persistence.repository.SpringBudgetRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class BudgetPersistenceAdapter implements BudgetRepositoryPort {

    private final SpringBudgetRepository repository;

    public BudgetPersistenceAdapter(SpringBudgetRepository repository) {
        this.repository = repository;
    }

    @Override
    public Budget save(Budget budget) {
        return repository.save(new BudgetJpaEntity(budget)).toDomain();
    }

    @Override
    public Optional<Budget> findById(UUID id) {
        return repository.findById(id).map(BudgetJpaEntity::toDomain);
    }

    @Override
    public Budget findByUserIdAndCategoryIdAndMonthAndYear(UUID userId, UUID categoryId, int month, int year) {
        return repository.findByUserIdAndCategoryIdAndMonthAndYear(userId, categoryId, month, year)
                .map(BudgetJpaEntity::toDomain)
                .orElse(null);
    }

    @Override
    public List<Budget> findByUserIdAndMonthAndYear(UUID userId, int month, int year) {
        return repository.findByUserIdAndMonthAndYear(userId, month, year).stream()
                .map(BudgetJpaEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Budget> findByUserId(UUID userId) {
        return repository.findByUserId(userId).stream()
                .map(BudgetJpaEntity::toDomain)
                .collect(Collectors.toList());
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