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
class ListAccountsByUserUseCaseTest {

    @Mock
    private AccountRepositoryPort accountRepositoryPort;

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @InjectMocks
    private ListAccountsByUserUseCase useCase;

    @Test
    @DisplayName("Deve listar todas as contas de um usuário existente")
    void shouldListAccountsWhenUserExists() {
        UUID userId = UUID.randomUUID();
        List<Account> accounts = List.of(
                new Account(UUID.randomUUID(), userId, AccountType.CHECKING, "Inter", new BigDecimal("100.00")),
                new Account(UUID.randomUUID(), userId, AccountType.INVESTMENT, "Tesouro", new BigDecimal("500.00"))
        );

        when(userRepositoryPort.findById(userId)).thenReturn(mock(User.class));
        when(accountRepositoryPort.findByUserId(userId)).thenReturn(accounts);

        List<Account> result = useCase.execute(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Inter", result.get(0).getName());
        verify(userRepositoryPort, times(1)).findById(userId);
        verify(accountRepositoryPort, times(1)).findByUserId(userId);
    }

    @Test
    @DisplayName("Deve lançar UserNotFoundException quando o usuário não existir")
    void shouldThrowExceptionWhenUserDoesNotExist() {
        UUID userId = UUID.randomUUID();

        when(userRepositoryPort.findById(userId)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> useCase.execute(userId));

        verify(accountRepositoryPort, never()).findByUserId(any());
    }
}