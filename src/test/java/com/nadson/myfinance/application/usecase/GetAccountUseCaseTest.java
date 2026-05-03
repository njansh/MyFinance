package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.enums.AccountType;
import com.nadson.myfinance.domain.exception.AccountNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAccountUseCaseTest {

    @Mock
    private AccountRepositoryPort accountRepositoryPort;

    @InjectMocks
    private GetAccountUseCase useCase;

    @Test
    @DisplayName("Deve retornar uma conta quando o ID existir")
    void shouldReturnAccountWhenIdExists() {
        UUID accountId = UUID.randomUUID();
        Account account = new Account(accountId, UUID.randomUUID(), AccountType.CHECKING, "Inter", BigDecimal.ZERO);

        when(accountRepositoryPort.findById(accountId)).thenReturn(account);

        Account result = useCase.execute(accountId);

        assertNotNull(result);
        assertEquals(accountId, result.getAccountId());
        assertEquals("Inter", result.getName());
        verify(accountRepositoryPort, times(1)).findById(accountId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando a conta não for encontrada")
    void shouldThrowExceptionWhenAccountNotFound() {
        UUID accountId = UUID.randomUUID();

        when(accountRepositoryPort.findById(accountId)).thenReturn(null);

        assertThrows(AccountNotFoundException.class, () -> useCase.execute(accountId));
        verify(accountRepositoryPort, times(1)).findById(accountId);
    }
}