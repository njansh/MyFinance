package com.nadson.myfinance.infrastructure.adapter.persistence.repository;

import com.nadson.myfinance.infrastructure.adapter.persistence.entity.CategoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public interface SpringCategoryRepository extends JpaRepository<CategoryJpaEntity, UUID> {
    Optional<CategoryJpaEntity> findByNameAndUserId(String name,UUID userID);

    Optional<CategoryJpaEntity> findAllByUserId(UUID userId);
}