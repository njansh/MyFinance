package com.nadson.myfinance.infrastructure.adapter.persistence.entity;

import com.nadson.myfinance.domain.entity.Goal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GoalJpaEntityTest {

    @Test
    @DisplayName("Should correctly map Domain Goal to JpaEntity and back to Domain")
    void shouldMapDomainToEntityAndBack() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        List<UUID> accounts = List.of(UUID.randomUUID(), UUID.randomUUID());

        Goal domainGoal = new Goal(id, userId, "Viagem", new BigDecimal("5000.00"), new BigDecimal("1000.00"), accounts);

        GoalJpaEntity entity = new GoalJpaEntity(domainGoal);

        Goal convertedDomain = entity.toDomain();

        assertThat(convertedDomain.getId()).isEqualTo(domainGoal.getId());
        assertThat(convertedDomain.getUserId()).isEqualTo(domainGoal.getUserId());
        assertThat(convertedDomain.getDescription()).isEqualTo(domainGoal.getDescription());
        assertThat(convertedDomain.getTargetAmount()).isEqualTo(domainGoal.getTargetAmount());
        assertThat(convertedDomain.getCurrentAmount()).isEqualTo(domainGoal.getCurrentAmount());
        assertThat(convertedDomain.getAccountIds()).containsExactlyElementsOf(domainGoal.getAccountIds());
    }
}