package com.nadson.myfinance.infrastructure.adapter.persistence.repository;

import com.nadson.myfinance.infrastructure.adapter.persistence.entity.BudgetJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringBudgetRepository extends JpaRepository<BudgetJpaEntity, UUID> {
    Optional<BudgetJpaEntity> findByUserIdAndCategoryIdAndMonthAndYear(UUID userId, UUID categoryId, int month, int year);
    List<BudgetJpaEntity> findByUserIdAndMonthAndYear(UUID userId, int month, int year);
    List<BudgetJpaEntity> findByUserId(UUID userId);    @Modifying
    @Query("DELETE FROM BudgetJpaEntity b WHERE b.userId = :userId")
    void deleteAllByUserId(@Param("userId") UUID userId);
}