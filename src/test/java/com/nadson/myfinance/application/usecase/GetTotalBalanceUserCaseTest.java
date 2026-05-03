package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
import com.nadson.myfinance.application.port.out.UserRepositoryPort;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.entity.User;
import com.nadson.myfinance.domain.enums.AccountType;
import com.nadson.myfinance.domain.exception.UserNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetTotalBalanceUserCaseTest {

    @Mock
    private AccountRepositoryPort accountRepositoryPort;

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @InjectMocks
    private GetTotalBalanceUserCase useCase;

    @Test
    @DisplayName("Deve calcular o saldo total somando todas as contas do usuário")
    void shouldCalculateTotalBalanceSuccessfully() {
        UUID userId = UUID.randomUUID();

        Account acc1 = new Account(UUID.randomUUID(), userId, AccountType.CHECKING, "Inter", new BigDecimal("1500.00"));
        Account acc2 = new Account(UUID.randomUUID(), userId, AccountType.INVESTMENT, "Tesouro", new BigDecimal("5000.00"));
        Account acc3 = new Account(UUID.randomUUID(), userId, AccountType.CASH, "Carteira", new BigDecimal("50.00"));

        when(userRepositoryPort.findById(userId)).thenReturn(mock(User.class));
        when(accountRepositoryPort.findByUserId(userId)).thenReturn(List.of(acc1, acc2, acc3));

        BigDecimal totalBalance = useCase.execute(userId);

        assertEquals(new BigDecimal("6550.00"), totalBalance);
        verify(accountRepositoryPort, times(1)).findByUserId(userId);
    }

    @Test
    @DisplayName("Deve retornar zero quando o usuário não possui contas")
    void shouldReturnZeroWhenUserHasNoAccounts() {
        UUID userId = UUID.randomUUID();

        when(userRepositoryPort.findById(userId)).thenReturn(mock(User.class));
        when(accountRepositoryPort.findByUserId(userId)).thenReturn(List.of());

        BigDecimal totalBalance = useCase.execute(userId);

        assertEquals(BigDecimal.ZERO, totalBalance);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o usuário não for encontrado")
    void shouldThrowExceptionWhenUserNotFound() {
        UUID userId = UUID.randomUUID();

        when(userRepositoryPort.findById(userId)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> useCase.execute(userId));
        verifyNoInteractions(accountRepositoryPort);
    }
}