package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.out.BudgetRepositoryPort;
import com.nadson.myfinance.domain.entity.Budget;
import com.nadson.myfinance.domain.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetBudgetUseCaseTest {
    @Mock
    private BudgetRepositoryPort repository;
    @InjectMocks private GetBudgetUseCase useCase;

    @Test
    @DisplayName("Deve retornar orçamento se encontrado")
    void shouldReturnBudget() {
        UUID id = UUID.randomUUID();
        Budget budget = mock(Budget.class);
        when(repository.findById(id)).thenReturn(Optional.of(budget));

        var result = useCase.execute(id);
        assertThat(result).isEqualTo(budget);
    }

    @Test
    @DisplayName("Deve lançar exceção se orçamento não existir")
    void shouldFailIfNotFound() {
        when(repository.findById(any())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> useCase.execute(UUID.randomUUID()));
    }
}
