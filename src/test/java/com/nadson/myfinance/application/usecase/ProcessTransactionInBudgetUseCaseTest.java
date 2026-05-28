package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
import com.nadson.myfinance.application.port.out.BudgetRepositoryPort;
import com.nadson.myfinance.domain.entity.Budget;
import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.domain.enums.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProcessTransactionInBudgetUseCaseTest {

    @Mock private BudgetRepositoryPort budgetRepository;
    @Mock private AccountRepositoryPort accountRepository;

    @InjectMocks
    private ProcessTransactionInBudgetUseCase useCase;

    @Test
    @DisplayName("Deve retornar null imediatamente se a transação não for uma despesa")
    void shouldReturnNullWhenTransactionIsNotExpense() {
        Transaction tx = mock(Transaction.class);
        when(tx.getType()).thenReturn(TransactionType.INCOME);

        String result = useCase.execute(tx);

        assertThat(result).isNull();
        verifyNoInteractions(accountRepository, budgetRepository);
    }

    @Test
    @DisplayName("Deve retornar null se não houver orçamento configurado para a categoria/período")
    void shouldReturnNullWhenBudgetNotFound() {
        Transaction tx = mock(Transaction.class);
        when(tx.getType()).thenReturn(TransactionType.EXPENSE);
        when(tx.getAccountId()).thenReturn(UUID.randomUUID());
        when(tx.getDate()).thenReturn(LocalDateTime.now());
        when(tx.getCategoryId()).thenReturn(UUID.randomUUID());

        when(accountRepository.findUserIdByAccountId(any())).thenReturn(UUID.randomUUID());
        when(budgetRepository.findByUserIdAndCategoryIdAndMonthAndYear(any(), any(), anyInt(), anyInt()))
                .thenReturn(null);

        String result = useCase.execute(tx);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Deve alertar quando o limite do orçamento for estourado (100%)")
    void shouldAlertWhenBudgetLimitExceeded() {
        Transaction tx = mock(Transaction.class);
        Budget budget = mock(Budget.class);

        when(tx.getType()).thenReturn(TransactionType.EXPENSE);
        when(tx.getAccountId()).thenReturn(UUID.randomUUID());
        when(tx.getDate()).thenReturn(LocalDateTime.now());
        when(tx.getAmount()).thenReturn(new BigDecimal("150.00"));

        when(accountRepository.findUserIdByAccountId(any())).thenReturn(UUID.randomUUID());
        when(budgetRepository.findByUserIdAndCategoryIdAndMonthAndYear(any(), any(), anyInt(), anyInt()))
                .thenReturn(budget);

        when(budget.shouldAlertOneHundredPercent()).thenReturn(true);

        String result = useCase.execute(tx);

        assertThat(result).isEqualTo("Budget limit exceeded!");
        verify(budget).addExpense(new BigDecimal("150.00"));
        verify(budgetRepository).save(budget);
    }

    @Test
    @DisplayName("Deve alertar quando o orçamento atingir 80% do limite")
    void shouldAlertWhenBudgetReachesEightyPercent() {
        Transaction tx = mock(Transaction.class);
        Budget budget = mock(Budget.class);

        when(tx.getType()).thenReturn(TransactionType.EXPENSE);
        when(tx.getAccountId()).thenReturn(UUID.randomUUID());
        when(tx.getDate()).thenReturn(LocalDateTime.now());
        when(tx.getAmount()).thenReturn(new BigDecimal("80.00"));

        when(accountRepository.findUserIdByAccountId(any())).thenReturn(UUID.randomUUID());
        when(budgetRepository.findByUserIdAndCategoryIdAndMonthAndYear(any(), any(), anyInt(), anyInt()))
                .thenReturn(budget);

        when(budget.shouldAlertOneHundredPercent()).thenReturn(false);
        when(budget.shouldAlertEightyPercent()).thenReturn(true);

        String result = useCase.execute(tx);

        assertThat(result).isEqualTo("You have reached 80% of your budget!");
        verify(budgetRepository).save(budget);
    }

    @Test
    @DisplayName("Deve processar a despesa sem retornar alertas se estiver abaixo dos limites")
    void shouldProcessWithoutAlertsWhenWithinLimits() {
        Transaction tx = mock(Transaction.class);
        Budget budget = mock(Budget.class);

        when(tx.getType()).thenReturn(TransactionType.EXPENSE);
        when(tx.getAccountId()).thenReturn(UUID.randomUUID());
        when(tx.getDate()).thenReturn(LocalDateTime.now());

        when(accountRepository.findUserIdByAccountId(any())).thenReturn(UUID.randomUUID());
        when(budgetRepository.findByUserIdAndCategoryIdAndMonthAndYear(any(), any(), anyInt(), anyInt()))
                .thenReturn(budget);

        when(budget.shouldAlertOneHundredPercent()).thenReturn(false);
        when(budget.shouldAlertEightyPercent()).thenReturn(false);

        String result = useCase.execute(tx);

        assertThat(result).isNull();
        verify(budgetRepository).save(budget);
    }
}