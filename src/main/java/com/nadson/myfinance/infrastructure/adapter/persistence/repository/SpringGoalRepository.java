package com.nadson.myfinance.infrastructure.adapter.persistence.repository;

import com.nadson.myfinance.infrastructure.adapter.persistence.entity.GoalJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpringGoalRepository extends JpaRepository<GoalJpaEntity, UUID> {
    List<GoalJpaEntity> findByUserId(UUID userId);
    @Modifying
    @Query("DELETE FROM GoalJpaEntity g WHERE g.userId = :userId")
    void deleteAllByUserId(@Param("userId") UUID userId);
}
