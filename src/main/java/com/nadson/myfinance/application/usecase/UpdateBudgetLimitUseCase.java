package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.UpdateBudgetLimitPort;
import com.nadson.myfinance.application.port.out.BudgetRepositoryPort;
import com.nadson.myfinance.domain.entity.Budget;
import com.nadson.myfinance.domain.exception.ResourceNotFoundException;
import java.math.BigDecimal;
import java.util.UUID;

public class UpdateBudgetLimitUseCase implements UpdateBudgetLimitPort {
    private final BudgetRepositoryPort repository;

    public UpdateBudgetLimitUseCase(BudgetRepositoryPort repository) {
        this.repository = repository;
    }
@Override
    public Budget execute(UUID budgetId, BigDecimal newLimit) {
        Budget budget = repository.findById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));

        Budget updatedBudget = new Budget(
                budget.getId(), budget.getUserId(), budget.getCategoryId(),
                budget.getMonth(), budget.getYear(), newLimit,
                budget.getSpentAmount(), false, false
        );

        return repository.save(updatedBudget);
    }
}