package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.CreateBudgetPort;
import com.nadson.myfinance.application.port.out.BudgetRepositoryPort;
import com.nadson.myfinance.application.port.out.CategoryRepositoryPort;
import com.nadson.myfinance.application.port.out.UserRepositoryPort;
import com.nadson.myfinance.domain.entity.Budget;
import com.nadson.myfinance.domain.exception.BusinessRuleException;

import java.math.BigDecimal;
import java.util.UUID;

public class CreateBudgetUseCase implements CreateBudgetPort {
    private final BudgetRepositoryPort repository;
    private final UserRepositoryPort userRepository;
    private final CategoryRepositoryPort categoryRepository;

    public CreateBudgetUseCase(BudgetRepositoryPort repository, UserRepositoryPort userRepository, CategoryRepositoryPort categoryRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Budget execute(UUID userId, UUID categoryId, int month, int year, BigDecimal limitAmount) {
        if (userRepository.findById(userId) == null) {
            throw new BusinessRuleException("User not found");
        }
        if (categoryRepository.findById(categoryId) == null) {
            throw new BusinessRuleException("Category not found");
        }


        Budget budget = new Budget(null, userId, categoryId, month, year, limitAmount);

            return repository.save(budget);
    }
}