package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.ProcessTransactionInBudgetPort;
import com.nadson.myfinance.application.port.in.ProcessTransactionInGoalPort;
import com.nadson.myfinance.application.port.in.RevertTransactionInGoalPort;
import com.nadson.myfinance.application.port.out.*;
import com.nadson.myfinance.domain.entity.*;
import com.nadson.myfinance.domain.enums.*;
import com.nadson.myfinance.domain.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class CreateAndUpdateTransactionUseCaseTest {

    @Mock private TransactionRepositoryPort txRepo;
    @Mock private AccountRepositoryPort accRepo;
    @Mock private CategoryRepositoryPort catRepo;
    @Mock private BudgetRepositoryPort budgetRepo;
    @Mock private ProcessTransactionInBudgetPort processBudget;
    @Mock private ProcessTransactionInGoalPort processGoal;
    @Mock private RevertTransactionInGoalPort revertGoal;

    private CreateTransactionUseCase createUseCase;
    private UpdateTransactionUseCase updateUseCase;

    @BeforeEach
    void setUp() {
        createUseCase = new CreateTransactionUseCase(txRepo, accRepo, catRepo, processBudget, processGoal);
        updateUseCase = new UpdateTransactionUseCase(txRepo, accRepo, budgetRepo, processBudget, revertGoal, processGoal);
    }

    // Helper para criar transações de forma menos repetitiva
    private Transaction createDummyTx(UUID accId, UUID catId, BigDecimal amount, TransactionType type) {
        return new Transaction(UUID.randomUUID(), "Desc", amount, LocalDateTime.now(), type, accId, catId, false, null, null, TransactionStatus.COMPLETED, null);
    }

    @Nested
    @DisplayName("Testes de Criação de Transação")
    class CreateTransactionTests {

        @Test
        @DisplayName("Deve criar receita e atualizar saldo (Caminho Feliz)")
        void shouldCreateIncomeTransaction() {
            UUID accId = UUID.randomUUID();
            Account account = new Account(accId, UUID.randomUUID(), AccountType.CHECKING, "Acc", BigDecimal.ZERO);
            Transaction tx = createDummyTx(accId, null, new BigDecimal("1000.00"), TransactionType.INCOME);

            when(accRepo.findById(accId)).thenReturn(account);
            when(txRepo.save(any())).thenAnswer(i -> i.getArgument(0));

            var result = createUseCase.execute(tx);

            verify(accRepo).updateBalanceAtomic(accId, new BigDecimal("1000.00"));
            assertThat(result.transaction().getAmount()).isEqualTo("1000.00");
        }

        @Test
        @DisplayName("Deve criar despesa e atualizar saldo negativamente")
        void shouldCreateExpenseTransaction() {
            UUID accId = UUID.randomUUID();
            Account account = new Account(accId, UUID.randomUUID(), AccountType.CHECKING, "Acc", BigDecimal.ZERO);
            Transaction tx = createDummyTx(accId, null, new BigDecimal("100.00"), TransactionType.EXPENSE);

            when(accRepo.findById(accId)).thenReturn(account);
            when(txRepo.save(any())).thenAnswer(i -> i.getArgument(0));

            createUseCase.execute(tx);

            verify(accRepo).updateBalanceAtomic(accId, new BigDecimal("-100.00"));
        }

        @Test
        @DisplayName("Deve falhar se valor for zero ou negativo")
        void shouldFailWhenAmountIsInvalid() {
            assertThrows(BusinessRuleException.class, () -> createDummyTx(UUID.randomUUID(), null, BigDecimal.ZERO, TransactionType.EXPENSE));
        }

        @Test
        @DisplayName("Deve falhar se a conta não for encontrada")
        void shouldFailWhenAccountNotFound() {
            Transaction tx = createDummyTx(UUID.randomUUID(), null, BigDecimal.TEN, TransactionType.EXPENSE);
            when(accRepo.findById(tx.getAccountId())).thenReturn(null);

            assertThrows(AccountNotFoundException.class, () -> createUseCase.execute(tx));
        }

        @Test
        @DisplayName("Deve falhar se a categoria não existir")
        void shouldFailWhenCategoryNotFound() {
            UUID accId = UUID.randomUUID();
            UUID catId = UUID.randomUUID();
            Transaction tx = createDummyTx(accId, catId, BigDecimal.TEN, TransactionType.EXPENSE);

            when(accRepo.findById(accId)).thenReturn(new Account(accId, UUID.randomUUID(), AccountType.CHECKING, "Acc", BigDecimal.ZERO));
            when(catRepo.findById(catId)).thenReturn(null);

            assertThrows(CategoryNotFoundException.class, () -> createUseCase.execute(tx));
        }

        @Test
        @DisplayName("Deve falhar se o tipo da categoria for incompatível")
        void shouldFailWhenCategoryTypeMismatch() {
            UUID accId = UUID.randomUUID();
            UUID catId = UUID.randomUUID();
            Transaction tx = createDummyTx(accId, catId, BigDecimal.TEN, TransactionType.EXPENSE);

            when(accRepo.findById(accId)).thenReturn(new Account(accId, UUID.randomUUID(), AccountType.CHECKING, "Acc", BigDecimal.ZERO));

            Category cat = mock(Category.class);
            when(cat.getType()).thenReturn(TransactionType.INCOME); // Categoria é RECEITA, Transação é DESPESA
            when(catRepo.findById(catId)).thenReturn(cat);

            assertThrows(BusinessRuleException.class, () -> createUseCase.execute(tx));
        }
    }

    @Nested
    @DisplayName("Testes de Atualização de Transação")
    class UpdateTransactionTests {

        @Test
        @DisplayName("Deve reverter saldo antigo e aplicar novo (Despesa -> Receita)")
        void shouldUpdateTransactionAndAdjustBalances() {
            UUID accId = UUID.randomUUID();
            UUID txId = UUID.randomUUID();
            Transaction oldTx = new Transaction(txId, "Old", new BigDecimal("100.00"), LocalDateTime.now(), TransactionType.EXPENSE, accId, null, false, null, null, TransactionStatus.COMPLETED, null);

            when(txRepo.findById(txId)).thenReturn(oldTx);
            when(txRepo.save(any())).thenAnswer(i -> i.getArgument(0));

            updateUseCase.execute(txId, "New", new BigDecimal("200.00"), LocalDateTime.now(), TransactionType.INCOME, accId, null);

            verify(accRepo).updateBalanceAtomic(accId, new BigDecimal("100.00")); // Reverte -100 virando +100
            verify(accRepo).updateBalanceAtomic(accId, new BigDecimal("200.00")); // Aplica +200
        }

        @Test
        @DisplayName("Deve reverter e aplicar saldo corretamente (Receita -> Receita)")
        void shouldHandleIncomeRevertAndApply() {
            UUID accId = UUID.randomUUID();
            UUID txId = UUID.randomUUID();
            Transaction oldTx = new Transaction(txId, "Old", new BigDecimal("100.00"), LocalDateTime.now(), TransactionType.INCOME, accId, null, false, null, null, TransactionStatus.COMPLETED, null);

            when(txRepo.findById(txId)).thenReturn(oldTx);
            when(txRepo.save(any())).thenAnswer(i -> i.getArgument(0));

            updateUseCase.execute(txId, "New", new BigDecimal("200.00"), LocalDateTime.now(), TransactionType.INCOME, accId, null);

            verify(accRepo).updateBalanceAtomic(accId, new BigDecimal("-100.00")); // Reverte +100 virando -100
            verify(accRepo).updateBalanceAtomic(accId, new BigDecimal("200.00"));  // Aplica +200
        }

        @Test
        @DisplayName("Deve atualizar transferência com sucesso e processar metas")
        void shouldHandleTransferUpdate() {
            UUID accId = UUID.randomUUID();
            UUID txId = UUID.randomUUID();
            UUID transferId = UUID.randomUUID();

            Transaction oldTx = mock(Transaction.class);
            when(oldTx.isTransfer()).thenReturn(true);
            when(oldTx.getTransferID()).thenReturn(transferId);
            when(oldTx.getAmount()).thenReturn(new BigDecimal("100.00"));
            when(oldTx.getType()).thenReturn(TransactionType.EXPENSE);
            when(oldTx.getAccountId()).thenReturn(accId);

            when(txRepo.findById(txId)).thenReturn(oldTx);
            when(txRepo.findAllByTransferID(transferId)).thenReturn(List.of(oldTx));
            when(txRepo.save(any())).thenAnswer(i -> i.getArgument(0));

            updateUseCase.execute(txId, "Transfer", new BigDecimal("50.00"), LocalDateTime.now(), TransactionType.EXPENSE, accId, null);

            verify(revertGoal).execute(oldTx);
            verify(processGoal).execute(any());
        }

        @Test
        @DisplayName("Deve remover despesa do orçamento antigo ao atualizar")
        void shouldRemoveExpenseFromOldBudget() {
            UUID accId = UUID.randomUUID();
            UUID txId = UUID.randomUUID();
            UUID catId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            Transaction oldTx = new Transaction(txId, "Old", new BigDecimal("100.00"), LocalDateTime.now(), TransactionType.EXPENSE, accId, catId, false, null, null, TransactionStatus.COMPLETED, null);
            Budget oldBudget = mock(Budget.class);

            when(txRepo.findById(txId)).thenReturn(oldTx);
            when(accRepo.findUserIdByAccountId(accId)).thenReturn(userId);
            when(budgetRepo.findByUserIdAndCategoryIdAndMonthAndYear(any(), any(), anyInt(), anyInt())).thenReturn(oldBudget);
            when(txRepo.save(any())).thenAnswer(i -> i.getArgument(0));

            updateUseCase.execute(txId, "New", new BigDecimal("50.00"), LocalDateTime.now(), TransactionType.EXPENSE, accId, catId);

            verify(oldBudget).removeExpense(new BigDecimal("100.00"));
            verify(budgetRepo).save(oldBudget);
        }
    }
}