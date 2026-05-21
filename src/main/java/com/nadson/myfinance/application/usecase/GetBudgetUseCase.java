package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.GetBudgetPort;
import com.nadson.myfinance.application.port.out.BudgetRepositoryPort;
import com.nadson.myfinance.domain.entity.Budget;
import com.nadson.myfinance.domain.exception.ResourceNotFoundException;
import java.util.UUID;

public class GetBudgetUseCase implements GetBudgetPort {
    private final BudgetRepositoryPort repository;

    public GetBudgetUseCase(BudgetRepositoryPort repository) {
        this.repository = repository;
    }

    public Budget execute(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));
    }
}