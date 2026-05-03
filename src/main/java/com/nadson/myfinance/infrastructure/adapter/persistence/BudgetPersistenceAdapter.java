package com.nadson.myfinance.infrastructure.adapter.persistence;

import com.nadson.myfinance.application.port.out.BudgetRepositoryPort;
import com.nadson.myfinance.domain.entity.Budget;
import com.nadson.myfinance.infrastructure.adapter.persistence.entity.BudgetJpaEntity;
import com.nadson.myfinance.infrastructure.adapter.persistence.repository.SpringBudgetRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

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
    public Budget findByUserIdAndCategoryIdAndMonthAndYear(UUID userId, UUID categoryId, int month, int year) {
        return repository.findByUserIdAndCategoryIdAndMonthAndYear(userId, categoryId, month, year)
                .map(BudgetJpaEntity::toDomain)
                .orElse(null);
    }

    @Override
    public void deleteAllByUserId(UUID userId) {
        repository.deleteAllByUserId(userId);
    }
}