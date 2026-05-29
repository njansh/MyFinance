package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.RevertTransactionInGoalPort;
import com.nadson.myfinance.application.port.out.*;
import com.nadson.myfinance.domain.entity.*;
import com.nadson.myfinance.domain.enums.*;
import com.nadson.myfinance.domain.exception.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteTransactionUseCaseTest {

    @Mock private TransactionRepositoryPort txRepo;
    @Mock private AccountRepositoryPort accRepo;
    @Mock private BudgetRepositoryPort budgetRepo;
    @Mock private RevertTransactionInGoalPort revertGoal;

    @InjectMocks
    private DeleteTransactionUseCase useCase;

    @Test
    @DisplayName("Deve deletar transação simples (Expense) e reverter saldo/orçamento")
    void shouldDeleteSimpleExpenseTransaction() {
        UUID txId = UUID.randomUUID();
        UUID accId = UUID.randomUUID();
        UUID catId = UUID.randomUUID();
        Transaction tx = new Transaction(txId, "Despesa", new BigDecimal("100.00"), LocalDateTime.now(), TransactionType.EXPENSE, accId, catId, false, null, null, TransactionStatus.COMPLETED, null);
        Account acc = new Account(accId, UUID.randomUUID(), AccountType.CHECKING, "Acc", BigDecimal.ZERO);
        Budget budget = mock(Budget.class);

        when(txRepo.findById(txId)).thenReturn(tx);
        when(accRepo.findById(accId)).thenReturn(acc);
        when(budgetRepo.findByUserIdAndCategoryIdAndMonthAndYear(any(), eq(catId), anyInt(), anyInt())).thenReturn(budget);

        useCase.execute(txId);

        verify(accRepo).save(acc);
        verify(budget).removeExpense(new BigDecimal("100.00"));
        verify(txRepo).deleteById(txId);
        verify(revertGoal).execute(tx);
    }

    @Test
    @DisplayName("Deve deletar todas as transações de uma transferência")
    void shouldDeleteAllTransferTransactions() {
        UUID txId = UUID.randomUUID();
        UUID transferId = UUID.randomUUID();
        Transaction tx = mock(Transaction.class);
        when(tx.isTransfer()).thenReturn(true);
        when(tx.getTransferID()).thenReturn(transferId);

        when(txRepo.findById(txId)).thenReturn(tx);
        when(txRepo.findAllByTransferID(transferId)).thenReturn(List.of(tx, tx));
        when(accRepo.findById(any())).thenReturn(mock(Account.class));

        useCase.execute(txId);

        verify(txRepo, times(2)).deleteById(any());
    }

    @Test
    @DisplayName("Deve falhar se a transação não existir")
    void shouldFailWhenTransactionNotFound() {
        when(txRepo.findById(any())).thenReturn(null);
        assertThrows(TransactionNotFoundException.class, () -> useCase.execute(UUID.randomUUID()));
    }

    @Test
    @DisplayName("Deve falhar se a conta da transação não existir")
    void shouldFailWhenAccountNotFoundDuringReverse() {
        Transaction tx = new Transaction(UUID.randomUUID(), "Desc", BigDecimal.TEN, LocalDateTime.now(), TransactionType.EXPENSE, UUID.randomUUID(), null, false, null, null, TransactionStatus.COMPLETED, null);
        when(txRepo.findById(any())).thenReturn(tx);
        when(accRepo.findById(any())).thenReturn(null);

        assertThrows(AccountNotFoundException.class, () -> useCase.execute(tx.getTransactionId()));
    }
}