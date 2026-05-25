package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
import com.nadson.myfinance.application.port.out.GoalRepositoryPort;
import com.nadson.myfinance.application.port.out.UserRepositoryPort;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.entity.Goal;
import com.nadson.myfinance.domain.entity.User;
import com.nadson.myfinance.domain.enums.AccountType;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
 class UpdateGoalUseCaseTest {

    @Mock private GoalRepositoryPort goalRepository;
    @Mock private UserRepositoryPort userRepository;
    @Mock private AccountRepositoryPort accountRepository;

    @InjectMocks
    private UpdateGoalUseCase useCase;

    @Test
    @DisplayName("Should update goal successfully when user is owner and accounts are valid")
    void shouldUpdateGoalSuccessfully() {
        UUID userId = UUID.randomUUID();
        UUID goalId = UUID.randomUUID();
        UUID accId = UUID.randomUUID();

        Goal goal = new Goal(goalId, userId, "Viagem", new BigDecimal("1000.00"), BigDecimal.ZERO, null);
        Account account = new Account(accId, userId, AccountType.CHECKING, "Conta", BigDecimal.ZERO);

        when(userRepository.findById(userId)).thenReturn(new User(userId, "Name", "email@test.com", "pass"));
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));
        when(accountRepository.findById(accId)).thenReturn(account);
        when(goalRepository.save(any(Goal.class))).thenAnswer(i -> i.getArguments()[0]);

        Goal updatedGoal = useCase.execute(userId, goalId, "Viagem Europa", new BigDecimal("5000.00"), List.of(accId));

        assertThat(updatedGoal.getDescription()).isEqualTo("Viagem Europa");
        assertThat(updatedGoal.getTargetAmount()).isEqualTo(new BigDecimal("5000.00"));
    }

    @Test
    @DisplayName("Should throw exception when account does not belong to user")
    void shouldThrowExceptionWhenAccountAccessDenied() {
        UUID userId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        UUID goalId = UUID.randomUUID();
        UUID accId = UUID.randomUUID();

        Goal goal = new Goal(goalId, userId, "Viagem", new BigDecimal("1000.00"), BigDecimal.ZERO, null);
        Account otherAccount = new Account(accId, otherUserId, AccountType.CHECKING, "Conta", BigDecimal.ZERO);

        when(userRepository.findById(userId)).thenReturn(new User(userId, "Name", "email@test.com", "pass"));
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));
        when(accountRepository.findById(accId)).thenReturn(otherAccount);

        assertThrows(BusinessRuleException.class, () ->
                useCase.execute(userId, goalId, "Nova Desc", BigDecimal.TEN, List.of(accId)));
    }
}