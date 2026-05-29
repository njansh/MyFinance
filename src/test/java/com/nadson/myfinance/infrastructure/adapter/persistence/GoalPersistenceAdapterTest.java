package com.nadson.myfinance.infrastructure.adapter.persistence;

import com.nadson.myfinance.domain.entity.Goal;
import com.nadson.myfinance.infrastructure.adapter.persistence.entity.GoalJpaEntity;
import com.nadson.myfinance.infrastructure.adapter.persistence.repository.SpringGoalRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GoalPersistenceAdapterTest {

    @Mock
    private SpringGoalRepository repository;

    @InjectMocks
    private GoalPersistenceAdapter adapter;

    @Test
    @DisplayName("Deve salvar meta")
    void shouldSaveGoal() {
        Goal goal = new Goal(UUID.randomUUID(),UUID.randomUUID() ,"Viagem",new BigDecimal("1000") ,new BigDecimal("5000.00"),new ArrayList<>());
        when(repository.save(any(GoalJpaEntity.class)))
                .thenReturn(new GoalJpaEntity(goal));
        Goal result = adapter.save(goal);
        assertThat(result).isNotNull();
        verify(repository).save(any(GoalJpaEntity.class));
    }

    @Test
    @DisplayName("Deve buscar meta por ID")
    void shouldFindById() {
        UUID id = UUID.randomUUID();
        GoalJpaEntity entity = mock(GoalJpaEntity.class);
        when(entity.toDomain()).thenReturn(mock(Goal.class));
        when(repository.findById(id)).thenReturn(Optional.of(entity));

        assertThat(adapter.findById(id)).isPresent();
    }

    @Test
    @DisplayName("Deve buscar metas por User ID")
    void shouldFindByUserId() {
        UUID userId = UUID.randomUUID();
        GoalJpaEntity entity = mock(GoalJpaEntity.class);
        when(entity.toDomain()).thenReturn(mock(Goal.class));
        when(repository.findByUserId(userId)).thenReturn(List.of(entity));

        assertThat(adapter.findByUserId(userId)).hasSize(1);
    }

    @Test
    @DisplayName("Deve buscar metas por Account ID")
    void shouldFindByAccountId() {
        UUID accId = UUID.randomUUID();
        GoalJpaEntity entity = mock(GoalJpaEntity.class);
        when(entity.toDomain()).thenReturn(mock(Goal.class));
        when(repository.findByAccountIdsContaining(accId)).thenReturn(List.of(entity));

        assertThat(adapter.findByAccountId(accId)).hasSize(1);
    }

    @Test
    @DisplayName("Deve deletar metas por ID e por User ID")
    void shouldDeleteGoals() {
        UUID id = UUID.randomUUID();
        adapter.deleteById(id);
        adapter.deleteAllByUserId(id);

        verify(repository).deleteById(id);
        verify(repository).deleteAllByUserId(id);
    }
}