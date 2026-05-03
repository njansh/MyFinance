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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateGoalUseCaseTest {

    @Mock
    private GoalRepositoryPort repository;

    @Mock
    private UserRepositoryPort userRepository;

    @InjectMocks
    private CreateGoalUseCase useCase;

    @Test
    @DisplayName("Deve criar uma meta financeira com sucesso")
    void shouldCreateGoalSuccessfully() {
        UUID userId = UUID.randomUUID();
        String description = "Viagem Japão";
        BigDecimal target = new BigDecimal("20000.00");

        when(userRepository.findById(userId)).thenReturn(mock(User.class));
        when(repository.save(any(Goal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Goal result = useCase.execute(userId, description, target);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(description, result.getDescription());
        assertEquals(target, result.getTargetAmount());
        assertEquals(BigDecimal.ZERO, result.getCurrentAmount());
        verify(repository, times(1)).save(any(Goal.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando o usuário da meta não existir")
    void shouldThrowExceptionWhenUserNotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(null);

        assertThrows(BusinessRuleException.class, () ->
                useCase.execute(userId, "Reserva", BigDecimal.ONE));

        verifyNoInteractions(repository);
    }
}