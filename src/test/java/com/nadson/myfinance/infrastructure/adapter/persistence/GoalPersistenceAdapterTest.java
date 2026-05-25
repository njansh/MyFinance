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
        // Criando uma entidade de domínio temporária para usar o construtor da UserJpaEntity
        User userDomain = new User(userId, "Test User", "test@test.com", "password123");
        userRepository.save(new UserJpaEntity(userDomain));
    }

    @Test
    @DisplayName("Should save and find goal by account ID")
    void shouldSaveAndFindByAccountId() {
        UUID accountId = UUID.randomUUID();
        Goal goal = new Goal(UUID.randomUUID(), userId, "Viagem", new BigDecimal("1000.00"), BigDecimal.ZERO, List.of(accountId));

        adapter.save(goal);
        List<Goal> foundGoals = adapter.findByAccountId(accountId);

        assertThat(foundGoals).hasSize(1);
        assertThat(foundGoals.get(0).getAccountIds()).contains(accountId);
    }

    @Test
    @DisplayName("Should delete all goals by user ID")
    void shouldDeleteAllByUserId() {
        Goal goal = new Goal(UUID.randomUUID(), userId, "Meta", new BigDecimal("500.00"), BigDecimal.ZERO, null);

        adapter.save(goal);
        adapter.deleteAllByUserId(userId);

        assertThat(adapter.findByUserId(userId)).isEmpty();
    }
}