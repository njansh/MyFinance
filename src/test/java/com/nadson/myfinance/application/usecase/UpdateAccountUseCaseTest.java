package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
import com.nadson.myfinance.application.port.out.GoalRepositoryPort;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.entity.Goal;
import com.nadson.myfinance.domain.enums.AccountType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateAccountUseCaseTest {

    @Mock private AccountRepositoryPort accountRepositoryPort;
    @Mock private GoalRepositoryPort goalRepositoryPort;

    @InjectMocks
    private UpdateAccountUseCase useCase;

    @Test
    @DisplayName("Deve falhar quando a conta não for encontrada")
    void shouldThrowExceptionWhenAccountNotFound() {
        UUID accountId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(accountRepositoryPort.findById(accountId)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                useCase.execute(accountId, userId, "Conta Principal", BigDecimal.TEN, "CHECKING"));

        assertThat(exception.getMessage()).isEqualTo("Account not found");
        verify(accountRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Deve falhar se o usuário não tiver permissão de acesso à conta")
    void shouldThrowExceptionWhenUserDoesNotHavePermission() {
        UUID accountId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID intruderId = UUID.randomUUID();

        Account accountMock = mock(Account.class);
        when(accountMock.getUserId()).thenReturn(userId); // Conta pertence a 'userId'
        when(accountRepositoryPort.findById(accountId)).thenReturn(accountMock);

        assertThrows(SecurityException.class, () ->
                useCase.execute(accountId, intruderId, "Conta Principal", BigDecimal.TEN, "CHECKING"));

        verify(accountRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar dados com sucesso sem alterar o saldo e mapeando tipo nulo")
    void shouldUpdateAccountSuccessfullyWithoutBalanceDeltaAndNullType() {
        UUID accountId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        BigDecimal initialBalance = new BigDecimal("500.00");

        Account accountMock = mock(Account.class);
        when(accountMock.getUserId()).thenReturn(userId);
        when(accountMock.getBalance()).thenReturn(initialBalance);
        when(accountRepositoryPort.findById(accountId)).thenReturn(accountMock);
        when(accountRepositoryPort.save(accountMock)).thenReturn(accountMock);

        // Executa enviando o exato mesmo saldo (delta = 0) e tipo nulo
        Account result = useCase.execute(accountId, userId, "Novo Nome", initialBalance, null);

        assertThat(result).isNotNull();
        verify(accountMock).update("Novo Nome", initialBalance, null);
        verifyNoInteractions(goalRepositoryPort);
    }

    @Test
    @DisplayName("Deve sincronizar adicionando o delta positivo nas metas quando o saldo aumentar")
    void shouldSyncGoalsWithPositiveDeltaWhenBalanceIncreases() {
        UUID accountId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        BigDecimal currentBalance = new BigDecimal("500.00");
        BigDecimal newBalance = new BigDecimal("650.00"); // Delta de +150.00
        BigDecimal expectedDelta = new BigDecimal("150.00");

        Account accountMock = mock(Account.class);
        when(accountMock.getUserId()).thenReturn(userId);
        when(accountMock.getBalance()).thenReturn(currentBalance);
        when(accountRepositoryPort.findById(accountId)).thenReturn(accountMock);
        when(accountRepositoryPort.save(accountMock)).thenReturn(accountMock);

        Goal goalMock = mock(Goal.class);
        when(goalRepositoryPort.findByAccountId(accountId)).thenReturn(List.of(goalMock));

        useCase.execute(accountId, userId, "Conta", newBalance, "CHECKING");

        verify(accountMock).update(eq("Conta"), eq(newBalance), any(AccountType.class));
        verify(goalMock).addAmount(expectedDelta);
        verify(goalMock, never()).subtractAmount(any());
        verify(goalRepositoryPort).save(goalMock);
    }

    @Test
    @DisplayName("Deve sincronizar subtraindo o valor absoluto do delta nas metas quando o saldo diminuir")
    void shouldSyncGoalsWithAbsoluteNegativeDeltaWhenBalanceDecreases() {
        UUID accountId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        BigDecimal currentBalance = new BigDecimal("500.00");
        BigDecimal newBalance = new BigDecimal("400.00"); // Delta de -100.00
        BigDecimal expectedAbsoluteDelta = new BigDecimal("100.00");

        Account accountMock = mock(Account.class);
        when(accountMock.getUserId()).thenReturn(userId);
        when(accountMock.getBalance()).thenReturn(currentBalance);
        when(accountRepositoryPort.findById(accountId)).thenReturn(accountMock);
        when(accountRepositoryPort.save(accountMock)).thenReturn(accountMock);

        Goal goalMock = mock(Goal.class);
        when(goalRepositoryPort.findByAccountId(accountId)).thenReturn(List.of(goalMock));

        useCase.execute(accountId, userId, "Conta", newBalance, "CHECKING");

        verify(goalMock).subtractAmount(expectedAbsoluteDelta);
        verify(goalMock, never()).addAmount(any());
        verify(goalRepositoryPort).save(goalMock);
    }
}