package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.out.BudgetRepositoryPort;
import com.nadson.myfinance.domain.entity.Budget;
import com.nadson.myfinance.domain.exception.BusinessRuleException;
import com.nadson.myfinance.domain.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteBudgetUseCaseTest {

    @Mock
    private BudgetRepositoryPort repository;

    @InjectMocks
    private DeleteBudgetUseCase useCase;

    @Test
    @DisplayName("Deve deletar orçamento com sucesso quando autorizado")
    void shouldDeleteBudgetSuccessfully() {
        UUID userId = UUID.randomUUID();
        UUID budgetId = UUID.randomUUID();
        Budget budget = mock(Budget.class);

        when(budget.getUserId()).thenReturn(userId);
        when(repository.findById(budgetId)).thenReturn(Optional.of(budget));

        useCase.execute(userId, budgetId);

        verify(repository).deleteById(budgetId);
    }

    @Test
    @DisplayName("Deve falhar se o orçamento não for encontrado")
    void shouldFailWhenBudgetNotFound() {
        UUID budgetId = UUID.randomUUID();
        when(repository.findById(budgetId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                useCase.execute(UUID.randomUUID(), budgetId));
    }

    @Test
    @DisplayName("Deve falhar se o usuário não for dono do orçamento")
    void shouldFailWhenUnauthorized() {
        UUID ownerId = UUID.randomUUID();
        UUID intruderId = UUID.randomUUID();
        UUID budgetId = UUID.randomUUID();
        Budget budget = mock(Budget.class);

        when(budget.getUserId()).thenReturn(ownerId);
        when(repository.findById(budgetId)).thenReturn(Optional.of(budget));

        assertThrows(BusinessRuleException.class, () ->
                useCase.execute(intruderId, budgetId));
    }
}