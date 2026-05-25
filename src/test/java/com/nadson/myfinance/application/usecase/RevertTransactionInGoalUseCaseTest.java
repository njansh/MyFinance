package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.out.GoalRepositoryPort;
import com.nadson.myfinance.domain.entity.Goal;
import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.domain.enums.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RevertTransactionInGoalUseCaseTest {

    @Mock private GoalRepositoryPort goalRepository;
    @InjectMocks private RevertTransactionInGoalUseCase useCase;

    @Test
    @DisplayName("Should subtract amount from goal when reverting an INCOME transaction")
    void shouldRevertIncome() {
        UUID accountId = UUID.randomUUID();
        // Meta com 100 de saldo atual
        Goal goal = new Goal(UUID.randomUUID(), UUID.randomUUID(), "Viagem", new BigDecimal("1000.00"), new BigDecimal("100.00"), List.of(accountId));
        Transaction transaction = new Transaction(accountId, new BigDecimal("100.00"), TransactionType.INCOME, "Renda", null, null, null);

        when(goalRepository.findByAccountId(accountId)).thenReturn(List.of(goal));

        useCase.execute(transaction);

        // Deve subtrair 100, voltando a 0
        verify(goalRepository).save(argThat(g -> g.getCurrentAmount().compareTo(BigDecimal.ZERO) == 0));
    }

    @Test
    @DisplayName("Should add amount to goal when reverting an EXPENSE transaction")
    void shouldRevertExpense() {
        UUID accountId = UUID.randomUUID();
        // Meta com 50 de saldo atual
        Goal goal = new Goal(UUID.randomUUID(), UUID.randomUUID(), "Viagem", new BigDecimal("1000.00"), new BigDecimal("50.00"), List.of(accountId));
        Transaction transaction = new Transaction(accountId, new BigDecimal("50.00"), TransactionType.EXPENSE, "Gasto", null, null, null);

        when(goalRepository.findByAccountId(accountId)).thenReturn(List.of(goal));

        useCase.execute(transaction);

        // Deve somar 50, voltando a 100
        verify(goalRepository).save(argThat(g -> g.getCurrentAmount().compareTo(new BigDecimal("100.00")) == 0));
    }

    @Test
    @DisplayName("Should do nothing when no goals are affected")
    void shouldDoNothingWhenNoGoalsFound() {
        UUID accountId = UUID.randomUUID();
        Transaction transaction = new Transaction(accountId, new BigDecimal("100.00"), TransactionType.INCOME, "Renda", null, null, null);

        // Simulando que não existem metas para essa conta
        when(goalRepository.findByAccountId(accountId)).thenReturn(List.of());

        // O método deve terminar sem erros
        assertDoesNotThrow(() -> useCase.execute(transaction));

        // Verifica que o save NUNCA foi chamado, pois não há metas para atualizar
        verify(goalRepository, never()).save(any());
    }
}