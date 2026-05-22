package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.DeleteBudgetPort;
import com.nadson.myfinance.application.port.out.BudgetRepositoryPort;
import com.nadson.myfinance.domain.entity.Budget;
import com.nadson.myfinance.domain.exception.ResourceNotFoundException;
import com.nadson.myfinance.domain.exception.BusinessRuleException;
import java.util.UUID;

public class DeleteBudgetUseCase implements DeleteBudgetPort {
    private final BudgetRepositoryPort repository;

    public DeleteBudgetUseCase(BudgetRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public void execute(UUID userId, UUID budgetId) {
        Budget budget = repository.findById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));

        if (!budget.getUserId().equals(userId)) {
            throw new BusinessRuleException("Unauthorized to delete this budget");
        }

        repository.deleteById(budgetId);
    }
}