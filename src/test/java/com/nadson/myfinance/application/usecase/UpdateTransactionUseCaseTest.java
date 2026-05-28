package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.ProcessTransactionInBudgetPort;
import com.nadson.myfinance.application.port.in.ProcessTransactionInGoalPort;
import com.nadson.myfinance.application.port.in.RevertTransactionInGoalPort;
import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
import com.nadson.myfinance.application.port.out.BudgetRepositoryPort;
import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
import com.nadson.myfinance.application.usecase.CreateTransactionUseCase.TransactionResult;
import com.nadson.myfinance.domain.entity.Budget;
import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.domain.enums.TransactionType;
import com.nadson.myfinance.domain.exception.TransactionNotFoundException;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateTransactionUseCaseTest {

    @Mock private TransactionRepositoryPort transactionRepo;
    @Mock private AccountRepositoryPort accountRepo;
    @Mock private BudgetRepositoryPort budgetRepo;
    @Mock private ProcessTransactionInBudgetPort processTransactionInBudget;
    @Mock private RevertTransactionInGoalPort revertTransactionInGoal;
    @Mock private ProcessTransactionInGoalPort processTransactionInGoal;

    @InjectMocks
    private UpdateTransactionUseCase useCase;

    @Test
    @DisplayName("1. Deve falhar se a transação original não existir")
    void shouldFailWhenTransactionNotFound() {
        UUID txId = UUID.randomUUID();
        when(transactionRepo.findById(txId)).thenReturn(null);

        assertThrows(TransactionNotFoundException.class, () ->
                useCase.execute(txId, "Desc", BigDecimal.TEN, LocalDateTime.now(), TransactionType.EXPENSE, UUID.randomUUID(), null));
    }

    @Test
    @DisplayName("2. Deve atualizar uma transferência com sucesso (Fluxo em lote)")
    void shouldUpdateTransferSuccessfully() {
        UUID txId = UUID.randomUUID();
        UUID transferId = UUID.randomUUID();

        Transaction txMock = mock(Transaction.class);
        when(txMock.isTransfer()).thenReturn(true);
        when(txMock.getTransferID()).thenReturn(transferId);
        when(txMock.getType()).thenReturn(TransactionType.EXPENSE); // Exercita parte do revert/apply
        when(txMock.getAmount()).thenReturn(BigDecimal.TEN);
        when(txMock.getAccountId()).thenReturn(UUID.randomUUID());

        when(transactionRepo.findById(txId)).thenReturn(txMock);
        when(transactionRepo.findAllByTransferID(transferId)).thenReturn(List.of(txMock));
        when(transactionRepo.save(any(Transaction.class))).thenReturn(txMock);

        TransactionResult result = useCase.execute(txId, "Nova Desc Transferência", new BigDecimal("50.00"), LocalDateTime.now(), TransactionType.EXPENSE, UUID.randomUUID(), null);

        assertThat(result.alert()).isEqualTo("Transfer updated successfully.");
        verify(revertTransactionInGoal).execute(txMock);
        verify(processTransactionInGoal).execute(txMock);
    }

    @Test
    @DisplayName("3. Deve atualizar uma despesa comum, limpando o orçamento antigo e aplicando alerta no novo")
    void shouldUpdateExpenseAndSyncBudgetAndGoal() {
        UUID txId = UUID.randomUUID();
        UUID oldCategoryId = UUID.randomUUID();
        UUID newCategoryId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        Transaction oldTx = mock(Transaction.class);
        when(oldTx.isTransfer()).thenReturn(false);
        when(oldTx.getType()).thenReturn(TransactionType.EXPENSE); // Antiga era despesa
        when(oldTx.getAmount()).thenReturn(new BigDecimal("100.00"));
        when(oldTx.getAccountId()).thenReturn(accountId);
        when(oldTx.getCategoryId()).thenReturn(oldCategoryId);
        when(oldTx.getDate()).thenReturn(now);

        Budget oldBudget = mock(Budget.class);

        when(transactionRepo.findById(txId)).thenReturn(oldTx);
        when(accountRepo.findUserIdByAccountId(accountId)).thenReturn(userId);
        when(budgetRepo.findByUserIdAndCategoryIdAndMonthAndYear(eq(userId), eq(oldCategoryId), anyInt(), anyInt())).thenReturn(oldBudget);
        when(transactionRepo.save(oldTx)).thenReturn(oldTx);
        when(processTransactionInBudget.execute(oldTx)).thenReturn("Budget limit exceeded!");

        TransactionResult result = useCase.execute(txId, "Ajuste", new BigDecimal("120.00"), now, TransactionType.EXPENSE, accountId, newCategoryId);

        assertThat(result.alert()).isEqualTo("Budget limit exceeded!");
        verify(oldBudget).removeExpense(new BigDecimal("100.00"));
        verify(budgetRepo).save(oldBudget);
        verify(accountRepo).updateBalanceAtomic(accountId, new BigDecimal("100.00")); // Reversão da despesa (+100)
        verify(accountRepo).updateBalanceAtomic(accountId, new BigDecimal("-120.00")); // Aplicação da nova (-120)
    }

    @Test
    @DisplayName("4. Deve atualizar uma receita e testar os caminhos contrários das funções privadas (INCOME)")
    void shouldUpdateIncomeTransactionToCoverAllPrivateBranches() {
        UUID txId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        Transaction oldTx = mock(Transaction.class);
        when(oldTx.isTransfer()).thenReturn(false);
        when(oldTx.getType()).thenReturn(TransactionType.INCOME); // Antiga era Receita (Inverte lógica das funções privadas)
        when(oldTx.getAmount()).thenReturn(new BigDecimal("200.00"));
        when(oldTx.getAccountId()).thenReturn(accountId);

        when(transactionRepo.findById(txId)).thenReturn(oldTx);
        when(transactionRepo.save(oldTx)).thenReturn(oldTx);

        // Atualiza mudando também o tipo para INCOME para forçar a cobertura de applyAccountBalance com INCOME
        useCase.execute(txId, "Salário Atualizado", new BigDecimal("250.00"), now, TransactionType.INCOME, accountId, null);

        verify(accountRepo).updateBalanceAtomic(accountId, new BigDecimal("-200.00")); // Reversão de receita antiga (-200)
        verify(accountRepo).updateBalanceAtomic(accountId, new BigDecimal("250.00")); // Aplicação de receita nova (+250)
        verifyNoInteractions(budgetRepo, processTransactionInBudget);
    }

    @Test
    @DisplayName("5. Deve prosseguir normalmente se a despesa antiga não possuir orçamento associado")
    void shouldProceedWhenOldBudgetNotFound() {
        UUID txId = UUID.randomUUID();
        UUID oldCategoryId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        Transaction oldTx = mock(Transaction.class);
        when(oldTx.isTransfer()).thenReturn(false);
        when(oldTx.getType()).thenReturn(TransactionType.EXPENSE);
        when(oldTx.getAmount()).thenReturn(BigDecimal.TEN);
        when(oldTx.getAccountId()).thenReturn(accountId);
        when(oldTx.getCategoryId()).thenReturn(oldCategoryId);
        when(oldTx.getDate()).thenReturn(now);

        when(transactionRepo.findById(txId)).thenReturn(oldTx);
        when(accountRepo.findUserIdByAccountId(accountId)).thenReturn(userId);

        // Retorna null simulando que o orçamento antigo não existe
        when(budgetRepo.findByUserIdAndCategoryIdAndMonthAndYear(eq(userId), eq(oldCategoryId), anyInt(), anyInt())).thenReturn(null);
        when(transactionRepo.save(oldTx)).thenReturn(oldTx);

        useCase.execute(txId, "Sem Orçamento", BigDecimal.ONE, now, TransactionType.INCOME, accountId, null);

        verify(budgetRepo, never()).save(any(Budget.class));
        verify(transactionRepo).save(oldTx);
    }
}