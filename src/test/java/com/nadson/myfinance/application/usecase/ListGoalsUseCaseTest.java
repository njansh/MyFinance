package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.out.GoalRepositoryPort;
import com.nadson.myfinance.application.port.out.UserRepositoryPort;
import com.nadson.myfinance.domain.entity.Goal;
import com.nadson.myfinance.domain.entity.User;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListGoalsUseCaseTest {

    @Mock private GoalRepositoryPort goalRepository;
    @Mock private UserRepositoryPort userRepository;

    @InjectMocks
    private ListGoalsUseCase useCase;

    @Test
    @DisplayName("Should return list of goals when user exists")
    void shouldListGoalsSuccessfully() {
        UUID userId = UUID.randomUUID();
        Goal goal = new Goal(UUID.randomUUID(), userId, "Viagem", new BigDecimal("100.00"), BigDecimal.ZERO, null);

        when(userRepository.findById(userId)).thenReturn(new User(userId, "Name", "email@test.com", "pass"));
        when(goalRepository.findByUserId(userId)).thenReturn(List.of(goal));

        List<Goal> result = useCase.execute(userId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDescription()).isEqualTo("Viagem");
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void shouldThrowExceptionWhenUserNotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(null);

        assertThrows(BusinessRuleException.class, () -> useCase.execute(userId));
    }
}