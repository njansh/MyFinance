package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.ProcessTransactionInBudgetPort;
import com.nadson.myfinance.application.port.in.ProcessTransactionInGoalPort;
import com.nadson.myfinance.application.port.in.RevertTransactionInGoalPort;
import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
import com.nadson.myfinance.application.port.out.BudgetRepositoryPort;
import com.nadson.myfinance.application.port.out.CategoryRepositoryPort;
import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.domain.enums.AccountType;
import com.nadson.myfinance.domain.enums.TransactionStatus;
import com.nadson.myfinance.domain.enums.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
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

    @Test
    @DisplayName("CreateTransaction: Should create income and update account balance")
    void shouldCreateIncomeTransaction() {
        CreateTransactionUseCase useCase = new CreateTransactionUseCase(
                txRepo, accRepo, catRepo, processBudget, processGoal);

        UUID accId = UUID.randomUUID();
        Account account = new Account(accId, UUID.randomUUID(), AccountType.CHECKING, "Acc", BigDecimal.ZERO);
        Transaction tx = new Transaction(UUID.randomUUID(), "Bonus", new BigDecimal("1000.00"), LocalDateTime.now(), TransactionType.INCOME, accId, null, false, null, null, TransactionStatus.COMPLETED, null);

        when(accRepo.findById(accId)).thenReturn(account);
        when(txRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        CreateTransactionUseCase.TransactionResult result = useCase.execute(tx);

        verify(accRepo).updateBalanceAtomic(accId, new BigDecimal("1000.00")); // Confirma saldo positivo
        verify(processGoal).execute(any(Transaction.class)); // Confirma impacto nas metas
        assertThat(result.transaction().getDescription()).isEqualTo("Bonus");
    }

    @Test
    @DisplayName("UpdateTransaction: Should revert old balance and apply new balance")
    void shouldUpdateTransactionAndAdjustBalances() {
        UpdateTransactionUseCase useCase = new UpdateTransactionUseCase(
                txRepo, accRepo, budgetRepo, processBudget, revertGoal, processGoal);

        UUID accId = UUID.randomUUID();
        UUID txId = UUID.randomUUID();
        Transaction oldTx = new Transaction(txId, "Old", new BigDecimal("100.00"), LocalDateTime.now(), TransactionType.EXPENSE, accId, null, false, null, null, TransactionStatus.COMPLETED, null);

        when(txRepo.findById(txId)).thenReturn(oldTx);
        when(txRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        // Mudando de Despesa (100) para Receita (200)
        CreateTransactionUseCase.TransactionResult result = useCase.execute(
                txId, "New", new BigDecimal("200.00"), LocalDateTime.now(), TransactionType.INCOME, accId, null);

        // Verifica reversão (-100 despesa revertida = +100)
        verify(accRepo).updateBalanceAtomic(accId, new BigDecimal("100.00"));
        // Verifica aplicação do novo (200 receita = +200)
        verify(accRepo).updateBalanceAtomic(accId, new BigDecimal("200.00"));

        assertThat(result.transaction().getType()).isEqualTo(TransactionType.INCOME);
    }
}