package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.UpdateBudgetLimitPort;
import com.nadson.myfinance.application.port.out.BudgetRepositoryPort;
import com.nadson.myfinance.domain.entity.Budget;
import com.nadson.myfinance.domain.exception.ResourceNotFoundException;
import com.nadson.myfinance.domain.exception.BusinessRuleException;

import java.math.BigDecimal;
import java.util.UUID;

public class UpdateBudgetLimitUseCase implements UpdateBudgetLimitPort {
    private final BudgetRepositoryPort repository;

    public UpdateBudgetLimitUseCase(BudgetRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public Budget execute(UUID userId, UUID budgetId, BigDecimal newLimit) {
        Budget budget = repository.findById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));

        if (!budget.getUserId().equals(userId)) {
            throw new BusinessRuleException("Unauthorized to modify this budget");
        }

        budget.updateLimit(newLimit);
        return repository.save(budget);
    }
}