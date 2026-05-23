package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.UpdateCategoryPort;
import com.nadson.myfinance.application.port.out.CategoryRepositoryPort;
import com.nadson.myfinance.domain.entity.Category;
import com.nadson.myfinance.domain.enums.TransactionType;

import java.util.UUID;

public class UpdateCategoryUseCase implements UpdateCategoryPort {

    private final CategoryRepositoryPort categoryRepositoryPort;

    public UpdateCategoryUseCase(CategoryRepositoryPort categoryRepositoryPort) {
        this.categoryRepositoryPort = categoryRepositoryPort;
    }

    @Override
    public Category execute(UUID userId, UUID categoryId, String name, String colorHex, String icon, TransactionType type) {
        Category existing = categoryRepositoryPort.findById(categoryId);

        if (existing == null || !existing.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Category not found or you don't have permission to edit it.");
        }

        Category updated = new Category(existing.getCategoryId(), userId, name, colorHex, icon, type);
        return categoryRepositoryPort.save(updated);
    }
}