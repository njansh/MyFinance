package com.nadson.myfinance.infrastructure.adapter.persistence;

import com.nadson.myfinance.domain.entity.Goal;
import com.nadson.myfinance.domain.entity.User; // Import do domínio para criar o DTO da Entity
import com.nadson.myfinance.infrastructure.adapter.persistence.entity.UserJpaEntity;
import com.nadson.myfinance.infrastructure.adapter.persistence.repository.SpringGoalRepository;
import com.nadson.myfinance.infrastructure.adapter.persistence.repository.SpringUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = "spring.flyway.enabled=false")
@Import(GoalPersistenceAdapter.class)
class GoalPersistenceAdapterTest {

    @Autowired
    private GoalPersistenceAdapter adapter;

    @Autowired
    private SpringUserRepository userRepository;

    private UUID userId;

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID();

        UserJpaEntity user = new UserJpaEntity();
        user.setId(userId);
        user.setName("Test User");
        user.setEmail("test@test.com");
        user.setPassword("password123");

        userRepository.save(user);
    }

    @Test
    @DisplayName("Should persist and retrieve goal by ID")
    void shouldPersistAndRetrieveGoalById() {

        // Arrange
        Goal goal = new Goal(
                UUID.randomUUID(),
                userId,
                "Trip",
                new BigDecimal("1000.00"),
                new BigDecimal("250.00"),
                List.of(UUID.randomUUID())
        );

        // Act
        adapter.save(goal);

        Goal foundGoal = adapter.findById(goal.getId()).orElseThrow();

        // Assert
        assertThat(foundGoal).isNotNull();
        assertThat(foundGoal.getId()).isEqualTo(goal.getId());
        assertThat(foundGoal.getUserId()).isEqualTo(userId);
        assertThat(foundGoal.getDescription()).isEqualTo("Trip");
        assertThat(foundGoal.getTargetAmount()).isEqualByComparingTo("1000.00");
        assertThat(foundGoal.getCurrentAmount()).isEqualByComparingTo("250.00");
        assertThat(foundGoal.getAccountIds())
                .containsExactlyElementsOf(goal.getAccountIds());
    }

    @Test
    @DisplayName("Should return goals associated with account ID")
    void shouldReturnGoalsAssociatedWithAccountId() {

        // Arrange
        UUID accountId = UUID.randomUUID();

        Goal goal = new Goal(
                UUID.randomUUID(),
                userId,
                "Travel",
                new BigDecimal("5000.00"),
                BigDecimal.ZERO,
                List.of(accountId)
        );

        adapter.save(goal);

        // Act
        List<Goal> goals = adapter.findByAccountId(accountId);

        // Assert
        assertThat(goals)
                .hasSize(1)
                .extracting(Goal::getId)
                .contains(goal.getId());
    }

    @Test
    @DisplayName("Should delete all goals by user ID")
    void shouldDeleteAllGoalsByUserId() {

        // Arrange
        Goal goal1 = new Goal(
                UUID.randomUUID(),
                userId,
                "Goal 1",
                new BigDecimal("100.00"),
                BigDecimal.ZERO,
                List.of()
        );

        Goal goal2 = new Goal(
                UUID.randomUUID(),
                userId,
                "Goal 2",
                new BigDecimal("200.00"),
                BigDecimal.ZERO,
                List.of()
        );

        adapter.save(goal1);
        adapter.save(goal2);

        // Act
        adapter.deleteAllByUserId(userId);

        // Assert
        assertThat(adapter.findByUserId(userId)).isEmpty();
    }

    @Test
    @DisplayName("Should return empty list when no goals are associated with account ID")
    void shouldReturnEmptyListWhenNoGoalsExistForAccountId() {

        // Arrange
        UUID accountId = UUID.randomUUID();

        // Act
        List<Goal> goals = adapter.findByAccountId(accountId);

        // Assert
        assertThat(goals).isEmpty();
    }
}