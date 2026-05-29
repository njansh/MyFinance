package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.out.BudgetRepositoryPort;
import com.nadson.myfinance.domain.entity.Budget;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListBudgetsUseCaseTest {

    @Mock
    private BudgetRepositoryPort repository;

    @InjectMocks
    private ListBudgetsUseCase useCase;

    @Test
    @DisplayName("Deve listar orçamentos por usuário, mês e ano com sucesso")
    void shouldListBudgetsByUserIdMonthAndYear() {
        // Arrange
        UUID userId = UUID.randomUUID();
        int month = 5;
        int year = 2026;
        List<Budget> expectedBudgets = List.of(mock(Budget.class), mock(Budget.class));

        when(repository.findByUserIdAndMonthAndYear(userId, month, year))
                .thenReturn(expectedBudgets);

        // Act
        List<Budget> result = useCase.execute(userId, month, year);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(expectedBudgets);
        verify(repository, times(1)).findByUserIdAndMonthAndYear(userId, month, year);
    }
}