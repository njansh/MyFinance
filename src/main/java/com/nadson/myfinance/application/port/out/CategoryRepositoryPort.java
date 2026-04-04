package com.nadson.myfinance.application.port.out;

import com.nadson.myfinance.domain.entity.Category;

import java.util.List;
import java.util.UUID;

public interface CategoryRepositoryPort {
    Category save(Category category);
    Category findById(UUID categoryId);
    List<Category> findAll();
}
