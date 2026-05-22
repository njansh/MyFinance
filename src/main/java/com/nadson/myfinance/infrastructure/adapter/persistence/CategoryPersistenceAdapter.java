package com.nadson.myfinance.infrastructure.adapter.persistence;

import com.nadson.myfinance.application.port.out.CategoryRepositoryPort;
import com.nadson.myfinance.domain.entity.Category;
import com.nadson.myfinance.infrastructure.adapter.persistence.entity.CategoryJpaEntity;
import com.nadson.myfinance.infrastructure.adapter.persistence.repository.SpringCategoryRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class CategoryPersistenceAdapter implements CategoryRepositoryPort {
    private final SpringCategoryRepository springCategoryRepository;

    public CategoryPersistenceAdapter(SpringCategoryRepository springCategoryRepository) {
        this.springCategoryRepository = springCategoryRepository;
    }

    @Override
    public Category save(Category category) {
        CategoryJpaEntity entity = new CategoryJpaEntity(category);
        return springCategoryRepository.save(entity).toDomain();
    }

    @Override
    public Category findById(UUID categoryId) {
        return springCategoryRepository.findById(categoryId)
                .map(CategoryJpaEntity::toDomain)
                .orElse(null);
    }

    @Override
    public List<Category> findAllByUserId(UUID userId) {
        return springCategoryRepository.findByUserId(userId).stream()
                .map(CategoryJpaEntity::toDomain)
                .toList();
    }
    @Override
    public Category findByNameAndUserId(String name,UUID userId) {
        return springCategoryRepository.findByNameAndUserId(name,userId )
                .map(CategoryJpaEntity::toDomain)
                .orElse(null);
    }

    @Override
    public void deleteAllByUserId(UUID userId) {
        springCategoryRepository.deleteAllByUserId(userId);
    }
    @Override
    public void deleteById(UUID id) {
        springCategoryRepository.deleteById(id);
    }
}
