package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
import com.nadson.myfinance.application.port.out.GoalRepositoryPort;
import com.nadson.myfinance.application.port.out.UserRepositoryPort;
import com.nadson.myfinance.domain.entity.Account;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateGoalUseCaseTest {

    @Mock private UserRepositoryPort userRepositoryPort;
    @Mock private GoalRepositoryPort goalRepositoryPort;
    @Mock private AccountRepositoryPort accountRepositoryPort;

    @InjectMocks
    private UpdateGoalUseCase useCase;

    @Test
    @DisplayName("Deve falhar se o usuário não for encontrado")
    void shouldFailWhenUserNotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepositoryPort.findById(userId)).thenReturn(null);

        assertThrows(BusinessRuleException.class, () ->
                useCase.execute(userId, UUID.randomUUID(), "Meta", BigDecimal.TEN, null));
    }

    @Test
    @DisplayName("Deve falhar se a meta não for encontrada")
    void shouldFailWhenGoalNotFound() {
        UUID userId = UUID.randomUUID();
        UUID goalId = UUID.randomUUID();
        when(userRepositoryPort.findById(userId)).thenReturn(mock(User.class));
        when(goalRepositoryPort.findById(goalId)).thenReturn(Optional.empty());

        assertThrows(BusinessRuleException.class, () ->
                useCase.execute(userId, goalId, "Meta", BigDecimal.TEN, null));
    }

    @Test
    @DisplayName("Deve falhar se a meta pertencer a outro usuário")
    void shouldFailWhenGoalBelongsToAnotherUser() {
        UUID userId = UUID.randomUUID();
        UUID goalId = UUID.randomUUID();
        Goal goalMock = mock(Goal.class);

        when(userRepositoryPort.findById(userId)).thenReturn(mock(User.class));
        when(goalRepositoryPort.findById(goalId)).thenReturn(Optional.of(goalMock));
        when(goalMock.getUserId()).thenReturn(UUID.randomUUID()); // Outro ID

        assertThrows(BusinessRuleException.class, () ->
                useCase.execute(userId, goalId, "Meta", BigDecimal.TEN, null));
    }

    @Test
    @DisplayName("Deve falhar se alguma conta vinculada não for encontrada")
    void shouldFailWhenLinkedAccountNotFound() {
        UUID userId = UUID.randomUUID();
        UUID goalId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        Goal goalMock = mock(Goal.class);

        when(userRepositoryPort.findById(userId)).thenReturn(mock(User.class));
        when(goalRepositoryPort.findById(goalId)).thenReturn(Optional.of(goalMock));
        when(goalMock.getUserId()).thenReturn(userId);
        when(accountRepositoryPort.findById(accountId)).thenReturn(null);

        assertThrows(BusinessRuleException.class, () ->
                useCase.execute(userId, goalId, "Meta", BigDecimal.TEN, List.of(accountId)));
    }

    @Test
    @DisplayName("Deve falhar se a conta vinculada pertencer a outro usuário")
    void shouldFailWhenAccountBelongsToAnotherUser() {
        UUID userId = UUID.randomUUID();
        UUID goalId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        Goal goalMock = mock(Goal.class);
        Account accountMock = mock(Account.class);

        when(userRepositoryPort.findById(userId)).thenReturn(mock(User.class));
        when(goalRepositoryPort.findById(goalId)).thenReturn(Optional.of(goalMock));
        when(goalMock.getUserId()).thenReturn(userId);
        when(accountRepositoryPort.findById(accountId)).thenReturn(accountMock);
        when(accountMock.getUserId()).thenReturn(UUID.randomUUID()); // Outro dono

        assertThrows(BusinessRuleException.class, () ->
                useCase.execute(userId, goalId, "Meta", BigDecimal.TEN, List.of(accountId)));
    }

    @Test
    @DisplayName("Deve atualizar e salvar a meta com sucesso")
    void shouldUpdateAndSaveGoalSuccessfully() {
        UUID userId = UUID.randomUUID();
        UUID goalId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        Goal goalMock = mock(Goal.class);
        Account accountMock = mock(Account.class);
        List<UUID> accounts = List.of(accountId);

        when(userRepositoryPort.findById(userId)).thenReturn(mock(User.class));
        when(goalRepositoryPort.findById(goalId)).thenReturn(Optional.of(goalMock));
        when(goalMock.getUserId()).thenReturn(userId);
        when(accountRepositoryPort.findById(accountId)).thenReturn(accountMock);
        when(accountMock.getUserId()).thenReturn(userId);
        when(goalRepositoryPort.save(goalMock)).thenReturn(goalMock);

        Goal result = useCase.execute(userId, goalId, "Nova Descrição", BigDecimal.TEN, accounts);

        assertThat(result).isEqualTo(goalMock);
        verify(goalMock).update("Nova Descrição", BigDecimal.TEN, accounts);
        verify(goalRepositoryPort).save(goalMock);
    }
}