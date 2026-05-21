package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.DeleteBudgetPort;
import com.nadson.myfinance.application.port.out.BudgetRepositoryPort;
import com.nadson.myfinance.domain.exception.ResourceNotFoundException;
import java.util.UUID;

public class DeleteBudgetUseCase implements DeleteBudgetPort {
    private final BudgetRepositoryPort repository;

    public DeleteBudgetUseCase(BudgetRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public void execute(UUID budgetId) {
        repository.findById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));
        repository.deleteById(budgetId);
    }
}