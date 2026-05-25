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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateGoalUseCaseTest {

    @Mock private GoalRepositoryPort goalRepository;
    @Mock private UserRepositoryPort userRepository;
    @Mock private AccountRepositoryPort accountRepository;

    @InjectMocks
    private CreateGoalUseCase useCase;

    @Test
    @DisplayName("Should create goal with summed balance from linked accounts")
    void shouldCreateGoalWithSummedBalance() {
        UUID userId = UUID.randomUUID();
        UUID acc1 = UUID.randomUUID();
        UUID acc2 = UUID.randomUUID();

        // Mocks configurados com o construtor correto: (id, userId, type, name, balance)
        when(userRepository.findById(userId)).thenReturn(new User(userId, "Name", "email@test.com", "pass"));
        when(accountRepository.findById(acc1)).thenReturn(
                new Account(acc1, userId, AccountType.CHECKING, "Acc1", new BigDecimal("100.00")));
        when(accountRepository.findById(acc2)).thenReturn(
                new Account(acc2, userId, AccountType.CHECKING, "Acc2", new BigDecimal("250.00")));

        when(goalRepository.save(any(Goal.class))).thenAnswer(i -> i.getArguments()[0]);

        Goal goal = useCase.execute(userId, "Viagem", new BigDecimal("1000.00"), List.of(acc1, acc2));

        assertThat(goal.getCurrentAmount()).isEqualTo(new BigDecimal("350.00"));
    }

    @Test
    @DisplayName("Should throw exception when user does not exist")
    void shouldThrowExceptionWhenUserNotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(null);

        assertThrows(BusinessRuleException.class, () ->
                useCase.execute(userId, "Teste", new BigDecimal("100.00"), null));
    }
}