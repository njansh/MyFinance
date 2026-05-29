package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
import com.nadson.myfinance.application.port.out.UserRepositoryPort;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.entity.User;
import com.nadson.myfinance.domain.enums.AccountType;
import com.nadson.myfinance.domain.exception.AccountNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountBasicUseCasesTest {

    @Mock private AccountRepositoryPort accountRepo;
    @Mock private UserRepositoryPort userRepo;

    @Test
    @DisplayName("CreateAccountUseCase: Should create and save account")
    void testCreateAccount() {
        CreateAccountUseCase useCase = new CreateAccountUseCase(accountRepo);
        when(accountRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        Account account = useCase.execute(UUID.randomUUID(), "Corrente", AccountType.CHECKING);
        assertThat(account.getName()).isEqualTo("Corrente");
    }

    @Test
    @DisplayName("GetAccountUseCase: Should throw exception if not found")
    void testGetAccount() {
        GetAccountUseCase useCase = new GetAccountUseCase(accountRepo);
        UUID id = UUID.randomUUID();
        when(accountRepo.findById(id)).thenReturn(null);

        assertThrows(AccountNotFoundException.class, () -> useCase.execute(id));
    }

    @Test
    @DisplayName("ListAccountsByUserUseCase: Should return list of accounts")
    void testListAccounts() {
        ListAccountsByUserUseCase useCase = new ListAccountsByUserUseCase(accountRepo, userRepo);
        UUID userId = UUID.randomUUID();

        // CORREÇÃO AQUI: Passando um e-mail válido ("test@email.com") e dados completos
        User validUser = new User(userId, "Test User", "test@email.com", "password123");

        when(userRepo.findById(userId)).thenReturn(validUser);
        when(accountRepo.findByUserId(userId)).thenReturn(List.of(new Account(userId, "Account 1", AccountType.CHECKING)));

        assertThat(useCase.execute(userId)).hasSize(1);
    }
}