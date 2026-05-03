package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.enums.AccountType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAccountUseCaseTest {

    @Mock
    private AccountRepositoryPort accountRepositoryPort;

    @InjectMocks
    private CreateAccountUseCase useCase;

    @Test
    @DisplayName("Deve criar uma conta com sucesso")
    void shouldCreateAccountSuccessfully() {
        UUID userId = UUID.randomUUID();
        String name = "Carteira";
        AccountType type = AccountType.CASH;

        when(accountRepositoryPort.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Account result = useCase.execute(userId, name, type);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(name, result.getName());
        assertEquals(type, result.getType());

        verify(accountRepositoryPort, times(1)).save(any(Account.class));
    }
}