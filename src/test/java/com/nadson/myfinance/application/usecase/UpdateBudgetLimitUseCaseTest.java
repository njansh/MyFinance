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

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateBudgetLimitUseCaseTest {

    @Mock
    private BudgetRepositoryPort repository;

    @InjectMocks
    private UpdateBudgetLimitUseCase useCase;

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando o orçamento não for encontrado")
    void shouldThrowExceptionWhenBudgetNotFound() {
        UUID userId = UUID.randomUUID();
        UUID budgetId = UUID.randomUUID();

        // Simula o retorno de um Optional vazio do repositório
        when(repository.findById(budgetId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                useCase.execute(userId, budgetId, new BigDecimal("500.00")));

        assertThat(exception.getMessage()).isEqualTo("Budget not found");
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar BusinessRuleException se o usuário não for o dono do orçamento")
    void shouldThrowExceptionWhenUserIsUnauthorized() {
        UUID realOwnerId = UUID.randomUUID();
        UUID intruderUserId = UUID.randomUUID();
        UUID budgetId = UUID.randomUUID();
        Budget budgetMock = mock(Budget.class);

        when(budgetMock.getUserId()).thenReturn(realOwnerId);
        when(repository.findById(budgetId)).thenReturn(Optional.of(budgetMock));

        // Tenta executar a ação usando um ID de usuário diferente do dono real
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () ->
                useCase.execute(intruderUserId, budgetId, new BigDecimal("500.00")));

        assertThat(exception.getMessage()).isEqualTo("Unauthorized to modify this budget");
        verify(budgetMock, never()).updateLimit(any());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar o limite do orçamento com sucesso quando os dados forem válidos")
    void shouldUpdateBudgetLimitSuccessfully() {
        UUID userId = UUID.randomUUID();
        UUID budgetId = UUID.randomUUID();
        BigDecimal newLimit = new BigDecimal("2500.00");
        Budget budgetMock = mock(Budget.class);

        when(budgetMock.getUserId()).thenReturn(userId);
        when(repository.findById(budgetId)).thenReturn(Optional.of(budgetMock));
        when(repository.save(budgetMock)).thenReturn(budgetMock);

        Budget result = useCase.execute(userId, budgetId, newLimit);

        // Validações de estado e comportamento
        assertThat(result).isNotNull().isEqualTo(budgetMock);
        verify(budgetMock, times(1)).updateLimit(newLimit);
        verify(repository, times(1)).save(budgetMock);
    }
}