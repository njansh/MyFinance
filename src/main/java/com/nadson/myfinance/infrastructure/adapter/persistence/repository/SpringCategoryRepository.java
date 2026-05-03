package com.nadson.myfinance.infrastructure.adapter.persistence.repository;

import com.nadson.myfinance.infrastructure.adapter.persistence.entity.CategoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface SpringCategoryRepository extends JpaRepository<CategoryJpaEntity, UUID> {
    Optional<CategoryJpaEntity> findByNameAndUserId(String name,UUID userID);

    Optional<CategoryJpaEntity> findAllByUserId(UUID userId);
    @Modifying
    @Query("DELETE FROM CategoryJpaEntity c WHERE c.userId = :userId")
    void deleteAllByUserId(@Param("userId") UUID userId);
}