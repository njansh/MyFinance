package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.ListBudgetsPort;
import com.nadson.myfinance.application.port.out.BudgetRepositoryPort;
import com.nadson.myfinance.domain.entity.Budget;
import java.util.List;
import java.util.UUID;

public class ListBudgetsUseCase implements ListBudgetsPort {
    private final BudgetRepositoryPort repository;

    public ListBudgetsUseCase(BudgetRepositoryPort repository) {
        this.repository = repository;
    }
@Override
    public List<Budget> execute(UUID userId, int month, int year) {
        return repository.findByUserIdAndMonthAndYear(userId, month, year);
    }
}