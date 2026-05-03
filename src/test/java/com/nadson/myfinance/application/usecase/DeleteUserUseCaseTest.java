package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.DeleteAccountPort;
import com.nadson.myfinance.application.port.out.*;
import com.nadson.myfinance.domain.entity.Account;
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

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteUserUseCaseTest {

    @Mock private UserRepositoryPort userRepo;
    @Mock private AccountRepositoryPort accountRepo;
    @Mock private CategoryRepositoryPort categoryRepo;
    @Mock private BudgetRepositoryPort budgetRepo;
    @Mock private GoalRepositoryPort goalRepo;
    @Mock private RecurringTemplateRepositoryPort recurringRepo;
    @Mock private DeleteAccountPort deleteAccountPort;

    @InjectMocks
    private DeleteUserUseCase useCase;

    @Test
    @DisplayName("Deve excluir todos os dados vinculados e o usuário por último")
    void shouldDeleteAllUserDataSuccessfully() {
        UUID userId = UUID.randomUUID();
        UUID account1Id = UUID.randomUUID();
        UUID account2Id = UUID.randomUUID();

        Account acc1 = new Account(account1Id, userId, AccountType.CHECKING, "Conta 1", BigDecimal.ZERO);
        Account acc2 = new Account(account2Id, userId, AccountType.CHECKING, "Conta 2", BigDecimal.ZERO);

        when(accountRepo.findByUserId(userId)).thenReturn(List.of(acc1, acc2));

        useCase.execute(userId);

        verify(deleteAccountPort, times(1)).execute(account1Id, userId);
        verify(deleteAccountPort, times(1)).execute(account2Id, userId);

        verify(recurringRepo, times(1)).deleteAllByUserId(userId);
        verify(goalRepo, times(1)).deleteAllByUserId(userId);
        verify(budgetRepo, times(1)).deleteAllByUserId(userId);
        verify(categoryRepo, times(1)).deleteAllByUserId(userId);

        verify(userRepo, times(1)).deleteById(userId);
    }

    @Test
    @DisplayName("Deve excluir usuário mesmo que ele não possua contas")
    void shouldDeleteUserEvenWithoutAccounts() {
        UUID userId = UUID.randomUUID();

        when(accountRepo.findByUserId(userId)).thenReturn(List.of());

        useCase.execute(userId);

        verify(deleteAccountPort, never()).execute(any(), any());
        verify(userRepo, times(1)).deleteById(userId);
    }
}