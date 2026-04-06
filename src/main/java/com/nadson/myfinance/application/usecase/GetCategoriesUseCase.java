package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.GetCategoriesPort;
import com.nadson.myfinance.application.port.out.CategoryRepositoryPort;
import com.nadson.myfinance.domain.entity.Category;

import java.util.List;

public class GetCategoriesUseCase implements GetCategoriesPort {
    private final CategoryRepositoryPort categoryRepositoryPort;
    public GetCategoriesUseCase(CategoryRepositoryPort categoryRepositoryPort) {
        this.categoryRepositoryPort = categoryRepositoryPort;
    }
    @Override
    public List<Category> execute() {
return categoryRepositoryPort.findAll();    }
}
