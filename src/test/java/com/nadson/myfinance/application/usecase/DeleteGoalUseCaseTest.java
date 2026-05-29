package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.out.GoalRepositoryPort;
import com.nadson.myfinance.application.port.out.UserRepositoryPort;
import com.nadson.myfinance.domain.entity.Goal;
import com.nadson.myfinance.domain.entity.User;
import com.nadson.myfinance.domain.exception.BusinessRuleException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteGoalUseCaseTest {

    @Mock private GoalRepositoryPort repository;
    @Mock private UserRepositoryPort userRepositoryPort;

    @InjectMocks
    private DeleteGoalUseCase useCase;

    @Test
    @DisplayName("Deve deletar meta com sucesso")
    void shouldDeleteGoalSuccessfully() {
        UUID userId = UUID.randomUUID();
        UUID goalId = UUID.randomUUID();
        Goal goal = mock(Goal.class);

        when(userRepositoryPort.findById(userId)).thenReturn(mock(User.class));
        when(goal.getUserId()).thenReturn(userId);
        when(repository.findById(goalId)).thenReturn(Optional.of(goal));

        useCase.execute(goalId, userId);

        verify(repository).deleteById(goalId);
    }

    @Test
    @DisplayName("Deve falhar se o usuário não for encontrado")
    void shouldFailWhenUserNotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepositoryPort.findById(userId)).thenReturn(null);

        assertThrows(BusinessRuleException.class, () ->
                useCase.execute(UUID.randomUUID(), userId));
    }

    @Test
    @DisplayName("Deve falhar se a meta não for encontrada")
    void shouldFailWhenGoalNotFound() {
        UUID userId = UUID.randomUUID();
        UUID goalId = UUID.randomUUID();

        when(userRepositoryPort.findById(userId)).thenReturn(mock(User.class));
        when(repository.findById(goalId)).thenReturn(Optional.empty());

        assertThrows(BusinessRuleException.class, () ->
                useCase.execute(goalId, userId));
    }

    @Test
    @DisplayName("Deve falhar se a meta pertencer a outro usuário")
    void shouldFailWhenUnauthorized() {
        UUID ownerId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID goalId = UUID.randomUUID();
        Goal goal = mock(Goal.class);

        when(userRepositoryPort.findById(userId)).thenReturn(mock(User.class));
        when(goal.getUserId()).thenReturn(ownerId);
        when(repository.findById(goalId)).thenReturn(Optional.of(goal));

        assertThrows(BusinessRuleException.class, () ->
                useCase.execute(goalId, userId));
    }
}