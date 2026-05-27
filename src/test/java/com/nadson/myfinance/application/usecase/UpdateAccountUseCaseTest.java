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

    @Mock private AccountRepositoryPort accountRepo;
    @Mock private GoalRepositoryPort goalRepo;

    @InjectMocks private UpdateAccountUseCase useCase;

    @Test
    @DisplayName("Should update account and sync positive delta with goal")
    void shouldUpdateAccountAndAddDeltaToGoal() {
        UUID accountId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Account account = new Account(accountId, userId, AccountType.CHECKING, "Old Name", new BigDecimal("100.00"));
        Goal goal = new Goal(UUID.randomUUID(), userId, "Goal", new BigDecimal("1000"), new BigDecimal("50.00"), List.of(accountId));

        when(accountRepo.findById(accountId)).thenReturn(account);
        when(accountRepo.save(any())).thenAnswer(i -> i.getArgument(0));
        when(goalRepo.findByAccountId(accountId)).thenReturn(List.of(goal));

        // Novo balanço é 150.00 (Delta = +50.00)
        Account updated = useCase.execute(accountId, userId, "New Name", new BigDecimal("150.00"), "CHECKING");

        assertThat(updated.getName()).isEqualTo("New Name");
        assertThat(goal.getCurrentAmount()).isEqualByComparingTo("100.00"); // 50 anterior + 50 de delta
        verify(goalRepo, times(1)).save(goal);
    }

    @Test
    @DisplayName("Should update account and sync negative delta with goal")
    void shouldUpdateAccountAndSubtractDeltaFromGoal() {
        UUID accountId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Account account = new Account(accountId, userId, AccountType.CHECKING, "Name", new BigDecimal("200.00"));
        Goal goal = new Goal(UUID.randomUUID(), userId, "Goal", new BigDecimal("1000"), new BigDecimal("150.00"), List.of(accountId));

        when(accountRepo.findById(accountId)).thenReturn(account);
        when(accountRepo.save(any())).thenAnswer(i -> i.getArgument(0));
        when(goalRepo.findByAccountId(accountId)).thenReturn(List.of(goal));

        // Novo balanço é 50.00 (Delta = -150.00)
        useCase.execute(accountId, userId, "Name", new BigDecimal("50.00"), "CHECKING");

        assertThat(goal.getCurrentAmount()).isEqualByComparingTo("0.00"); // 150 - 150 abs
        verify(goalRepo, times(1)).save(goal);
    }

    @Test
    @DisplayName("Should throw SecurityException when user is not the owner")
    void shouldThrowExceptionWhenUserNotOwner() {
        UUID accountId = UUID.randomUUID();
        Account account = new Account(accountId, UUID.randomUUID(), AccountType.CHECKING, "Name", BigDecimal.ZERO);
        when(accountRepo.findById(accountId)).thenReturn(account);

        assertThrows(SecurityException.class, () ->
                useCase.execute(accountId, UUID.randomUUID(), "Name", BigDecimal.ZERO, "CHECKING"));
    }
}