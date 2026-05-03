package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.DeleteAccountPort;
import com.nadson.myfinance.application.port.out.*;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.enums.AccountType;
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

    @InjectMocks private DeleteUserUseCase useCase;

    @Test
    void shouldWipeAllUserDataWhenUserIsDeleted() {
        UUID userId = UUID.randomUUID();
        Account acc = new Account(UUID.randomUUID(), userId, AccountType.CHECKING, "Acc", BigDecimal.ZERO);
        when(accountRepo.findByUserId(userId)).thenReturn(List.of(acc));

        useCase.execute(userId);

        verify(deleteAccountPort).execute(eq(acc.getAccountId()), eq(userId));
        verify(recurringRepo).deleteAllByUserId(userId);
        verify(goalRepo).deleteAllByUserId(userId);
        verify(budgetRepo).deleteAllByUserId(userId);
        verify(categoryRepo).deleteAllByUserId(userId);
        verify(userRepo).deleteById(userId);
    }
}