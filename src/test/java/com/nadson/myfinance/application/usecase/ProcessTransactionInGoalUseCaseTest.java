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

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessTransactionInGoalUseCaseTest {

    @Mock
    private GoalRepositoryPort goalRepositoryPort;

    @InjectMocks
    private ProcessTransactionInGoalUseCase useCase;

    @Test
    @DisplayName("Deve retornar imediatamente se a transação for nula")
    void shouldReturnImmediatelyWhenTransactionIsNull() {
        useCase.execute(null);
        verifyNoInteractions(goalRepositoryPort);
    }

    @Test
    @DisplayName("Deve retornar imediatamente se o ID da conta for nulo")
    void shouldReturnImmediatelyWhenAccountIdIsNull() {
        Transaction tx = mock(Transaction.class);
        when(tx.getAccountId()).thenReturn(null);

        useCase.execute(tx);

        verifyNoInteractions(goalRepositoryPort);
    }

    @Test
    @DisplayName("Deve retornar imediatamente se a lista de metas afetadas for nula")
    void shouldReturnImmediatelyWhenAffectedGoalsIsNull() {
        UUID accountId = UUID.randomUUID();
        Transaction tx = mock(Transaction.class);
        when(tx.getAccountId()).thenReturn(accountId);
        when(goalRepositoryPort.findByAccountId(accountId)).thenReturn(null);

        useCase.execute(tx);

        verify(goalRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Deve retornar imediatamente se a lista de metas afetadas estiver vazia")
    void shouldReturnImmediatelyWhenAffectedGoalsIsEmpty() {
        UUID accountId = UUID.randomUUID();
        Transaction tx = mock(Transaction.class);
        when(tx.getAccountId()).thenReturn(accountId);
        when(goalRepositoryPort.findByAccountId(accountId)).thenReturn(List.of());

        useCase.execute(tx);

        verify(goalRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Deve adicionar valor às metas quando a transação for uma receita")
    void shouldAddAmountToGoalsWhenTransactionIsIncome() {
        UUID accountId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("250.00");

        Transaction tx = mock(Transaction.class);
        when(tx.getAccountId()).thenReturn(accountId);
        when(tx.getType()).thenReturn(TransactionType.INCOME);
        when(tx.getAmount()).thenReturn(amount);

        Goal goal = mock(Goal.class);
        when(goalRepositoryPort.findByAccountId(accountId)).thenReturn(List.of(goal));

        useCase.execute(tx);

        verify(goal).addAmount(amount);
        verify(goalRepositoryPort).save(goal);
    }

    @Test
    @DisplayName("Deve subtrair valor das metas quando a transação for uma despesa")
    void shouldSubtractAmountFromGoalsWhenTransactionIsExpense() {
        UUID accountId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("120.00");

        Transaction tx = mock(Transaction.class);
        when(tx.getAccountId()).thenReturn(accountId);
        when(tx.getType()).thenReturn(TransactionType.EXPENSE);
        when(tx.getAmount()).thenReturn(amount);

        Goal goal = mock(Goal.class);
        when(goalRepositoryPort.findByAccountId(accountId)).thenReturn(List.of(goal));

        useCase.execute(tx);

        verify(goal).subtractAmount(amount);
        verify(goalRepositoryPort).save(goal);
    }
}