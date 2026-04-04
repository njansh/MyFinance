package com.nadson.myfinance.infrastructure.adapter.persistence.repository;

import com.nadson.myfinance.infrastructure.adapter.persistence.entity.CategoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringCategoryRepository extends JpaRepository<CategoryJpaEntity, UUID>
{

}
