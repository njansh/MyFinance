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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessTransactionInGoalUseCaseTest {

    @Mock private GoalRepositoryPort goalRepository;
    @InjectMocks private ProcessTransactionInGoalUseCase useCase;

    @Test
    @DisplayName("Should add amount to goal when transaction is INCOME")
    void shouldProcessIncome() {
        UUID accountId = UUID.randomUUID();
        Goal goal = new Goal(UUID.randomUUID(), UUID.randomUUID(), "Viagem", new BigDecimal("1000.00"), BigDecimal.ZERO, List.of(accountId));

        // Construtor completo conforme a classe Transaction
        Transaction transaction = new Transaction(accountId, new BigDecimal("100.00"), TransactionType.INCOME, "Renda", null, null, null);

        when(goalRepository.findByAccountId(accountId)).thenReturn(List.of(goal));

        useCase.execute(transaction);

        verify(goalRepository).save(argThat(g -> g.getCurrentAmount().compareTo(new BigDecimal("100.00")) == 0));
    }

    @Test
    @DisplayName("Should subtract amount from goal when transaction is EXPENSE")
    void shouldProcessExpense() {
        UUID accountId = UUID.randomUUID();
        Goal goal = new Goal(UUID.randomUUID(), UUID.randomUUID(), "Viagem", new BigDecimal("1000.00"), new BigDecimal("200.00"), List.of(accountId));

        Transaction transaction = new Transaction(accountId, new BigDecimal("50.00"), TransactionType.EXPENSE, "Gasto", null, null, null);

        when(goalRepository.findByAccountId(accountId)).thenReturn(List.of(goal));

        useCase.execute(transaction);

        verify(goalRepository).save(argThat(g -> g.getCurrentAmount().compareTo(new BigDecimal("150.00")) == 0));
    }
    @Test
    @DisplayName("Should rollback changes if repository fails during batch processing")
    void shouldRollbackWhenRepositoryFails() {
        UUID accountId = UUID.randomUUID();

        Goal goal1 = new Goal(UUID.randomUUID(), UUID.randomUUID(), "Viagem", new BigDecimal("1000.00"), BigDecimal.ZERO, List.of(accountId));
        Goal goal2 = new Goal(UUID.randomUUID(), UUID.randomUUID(), "Carro", new BigDecimal("5000.00"), BigDecimal.ZERO, List.of(accountId));

        Transaction transaction = new Transaction(accountId, new BigDecimal("100.00"), TransactionType.INCOME, "Renda", null, null, null);

        when(goalRepository.findByAccountId(accountId)).thenReturn(List.of(goal1, goal2));

        when(goalRepository.save(goal1)).thenAnswer(i -> i.getArguments()[0]);
        when(goalRepository.save(goal2)).thenThrow(new RuntimeException("Database down"));

        assertThrows(RuntimeException.class, () -> useCase.execute(transaction));

        verify(goalRepository, times(1)).save(goal1);
        verify(goalRepository, times(1)).save(goal2);
    }}