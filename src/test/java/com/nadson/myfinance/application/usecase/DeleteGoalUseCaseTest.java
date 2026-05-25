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

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteGoalUseCaseTest {

    @Mock private GoalRepositoryPort goalRepository;
    @Mock private UserRepositoryPort userRepository;

    @InjectMocks
    private DeleteGoalUseCase useCase;

    @Test
    @DisplayName("Should delete goal successfully when user is owner")
    void shouldDeleteSuccessfully() {
        UUID userId = UUID.randomUUID();
        UUID goalId = UUID.randomUUID();
        Goal goal = new Goal(goalId, userId, "Viagem", new BigDecimal("100.00"), BigDecimal.ZERO, Collections.emptyList());

        when(userRepository.findById(userId)).thenReturn(new User(userId, "Name", "email@test.com", "pass"));
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));

        assertDoesNotThrow(() -> useCase.execute(goalId, userId));
        verify(goalRepository, times(1)).deleteById(goalId);
    }

    @Test
    @DisplayName("Should throw exception when user tries to delete another user's goal")
    void shouldThrowExceptionWhenUnauthorized() {
        UUID ownerId = UUID.randomUUID();
        UUID attackerId = UUID.randomUUID();
        UUID goalId = UUID.randomUUID();
        Goal goal = new Goal(goalId, ownerId, "Meta", new BigDecimal("100.00"), BigDecimal.ZERO, Collections.emptyList());

        when(userRepository.findById(attackerId)).thenReturn(new User(attackerId, "Attacker", "a@test.com", "pass"));
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));

        assertThrows(BusinessRuleException.class, () -> useCase.execute(goalId, attackerId));
        verify(goalRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should throw exception when goal does not exist")
    void shouldThrowExceptionWhenGoalNotFound() {
        UUID userId = UUID.randomUUID();
        UUID goalId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(new User(userId, "Name", "email@test.com", "pass"));
        when(goalRepository.findById(goalId)).thenReturn(Optional.empty());

        assertThrows(BusinessRuleException.class, () -> useCase.execute(goalId, userId));
    }
}