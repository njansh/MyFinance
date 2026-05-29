package com.nadson.myfinance.infrastructure.adapter.persistence.entity;

import com.nadson.myfinance.domain.entity.Goal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GoalJpaEntityTest {
    @Test
    @DisplayName("Cobertura 100% GoalJpaEntity")
    void testGoal() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        List<UUID> accountIds = List.of(UUID.randomUUID());
        Goal domain = new Goal(id, userId, "Teste", BigDecimal.TEN, BigDecimal.ZERO, accountIds);

        // Test Empty Constructor and all Setters
        GoalJpaEntity entityEmpty = new GoalJpaEntity();
        entityEmpty.setId(id);
        entityEmpty.setUserId(userId);
        entityEmpty.setDescription("Teste");
        entityEmpty.setTargetAmount(BigDecimal.TEN);
        entityEmpty.setCurrentAmount(BigDecimal.ZERO);
        entityEmpty.setAccountIds(accountIds);

        // Test Constructor with Domain
        GoalJpaEntity entity = new GoalJpaEntity(domain);

        // Test all Getters
        assertEquals(id, entity.getId());
        assertEquals(userId, entity.getUserId());
        assertEquals("Teste", entity.getDescription());
        assertEquals(BigDecimal.TEN, entity.getTargetAmount());
        assertEquals(BigDecimal.ZERO, entity.getCurrentAmount());
        assertEquals(accountIds, entity.getAccountIds());

        // Test toDomain
        assertEquals(id, entity.toDomain().getId());
    }
}