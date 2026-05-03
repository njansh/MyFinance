package com.nadson.myfinance.infrastructure.adapter.persistence.repository;

import com.nadson.myfinance.infrastructure.adapter.persistence.entity.RecurringTemplateJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringRecurringTemplateRepository extends JpaRepository<RecurringTemplateJpaEntity, UUID> {
    Optional<RecurringTemplateJpaEntity> findById(UUID id);

    @Query("SELECT t FROM RecurringTemplateJpaEntity t WHERE t.userId = :userId AND t.active = true " +
            "AND t.frequencyDay <= :currentDay " +
            "AND (t.lastExecutedMonth IS NULL OR t.lastExecutedYear < :currentYear " +
            "OR (t.lastExecutedYear = :currentYear AND t.lastExecutedMonth < :currentMonth))")
    List<RecurringTemplateJpaEntity> findPendingTemplates(
            @Param("userId") UUID userId,
            @Param("currentDay") int currentDay,
            @Param("currentMonth") int currentMonth,
            @Param("currentYear") int currentYear
    );
    @Modifying
    @Query("DELETE FROM RecurringTemplateJpaEntity r WHERE r.userId = :userId")
    void deleteAllByUserId(@Param("userId") UUID userId);

    @Modifying
    @Query("DELETE FROM RecurringTemplateJpaEntity r WHERE r.accountId = :accountId")
    void deleteAllByAccountId(@Param("accountId") UUID accountId);
}
