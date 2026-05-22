package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.DeleteCategoryPort;
import com.nadson.myfinance.application.port.out.CategoryRepositoryPort;
import com.nadson.myfinance.domain.entity.Category;
import com.nadson.myfinance.domain.exception.BusinessRuleException;

import java.util.UUID;

public class DeleteCategoryUseCase implements DeleteCategoryPort {
    private final CategoryRepositoryPort repository;

    public DeleteCategoryUseCase(CategoryRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public void execute(UUID userId, UUID categoryId) {
        Category category = repository.findById(categoryId);

        if (category == null || !category.getUserId().equals(userId)) {
            throw new BusinessRuleException("Category not found or access denied.");
        }

        repository.deleteById(categoryId);
    }
}
