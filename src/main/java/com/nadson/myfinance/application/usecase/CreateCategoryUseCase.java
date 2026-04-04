package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.CreateCategoryPort;
import com.nadson.myfinance.application.port.out.CategoryRepositoryPort;
import com.nadson.myfinance.domain.entity.Category;

public class CreateCategoryUseCase implements CreateCategoryPort {

    private final CategoryRepositoryPort categoryRepositoryPort;

    public CreateCategoryUseCase(CategoryRepositoryPort categoryRepositoryPort) {
        this.categoryRepositoryPort = categoryRepositoryPort;
    }

    @Override
    public Category execute(String categoryName, String colorhex) {
Category category=new Category(null,categoryName,colorhex);

        return categoryRepositoryPort.save(category);
    }
}
