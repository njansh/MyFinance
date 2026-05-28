package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.ProcessTransactionInBudgetPort;
import com.nadson.myfinance.application.port.in.ProcessTransactionInGoalPort;
import com.nadson.myfinance.application.port.in.RevertTransactionInGoalPort;
import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
import com.nadson.myfinance.application.port.out.BudgetRepositoryPort;
import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.domain.enums.TransactionType;
import com.nadson.myfinance.domain.exception.BusinessRuleException;
import com.nadson.myfinance.domain.exception.TransactionNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
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
    @DisplayName("Deve falhar se a transação não existir")
    void shouldFailWhenTransactionNotFound() {
        UUID id = UUID.randomUUID();
        when(transactionRepo.findById(id)).thenReturn(null);
        assertThrows(TransactionNotFoundException.class, () ->
                useCase.execute(id, "Desc", BigDecimal.TEN, LocalDateTime.now(), TransactionType.EXPENSE, UUID.randomUUID(), null));
    }

    @Test
    @DisplayName("Deve falhar se tentar atualizar uma transferência (não suportado neste caso de uso)")
    void shouldFailWhenTransactionIsTransfer() {
        UUID id = UUID.randomUUID();
        Transaction oldTx = mock(Transaction.class);
        when(oldTx.isTransfer()).thenReturn(true);
        when(transactionRepo.findById(id)).thenReturn(oldTx);

        assertThrows(BusinessRuleException.class, () ->
                useCase.execute(id, "Desc", BigDecimal.TEN, LocalDateTime.now(), TransactionType.EXPENSE, UUID.randomUUID(), null));
    }

    @Test
    @DisplayName("Deve atualizar transação comum com sucesso")
    void shouldUpdateStandardTransactionSuccessfully() {
        UUID id = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        
        Transaction oldTx = mock(Transaction.class);
        when(oldTx.isTransfer()).thenReturn(false);
        when(oldTx.getType()).thenReturn(TransactionType.EXPENSE);
        when(oldTx.getAmount()).thenReturn(BigDecimal.TEN);
        when(oldTx.getAccountId()).thenReturn(accountId);
        when(oldTx.getDate()).thenReturn(now);

        when(transactionRepo.findById(id)).thenReturn(oldTx);
        when(transactionRepo.save(any(Transaction.class))).thenReturn(oldTx);

        useCase.execute(id, "Nova Desc", new BigDecimal("20.00"), now, TransactionType.EXPENSE, categoryId, null);

        // Verifica reversão do estado anterior
        verify(revertTransactionInGoal).execute(oldTx);
        verify(accountRepo).updateBalanceAtomic(accountId, BigDecimal.TEN); // Estorna despesa antiga (+10)

        // Verifica atualização para novo estado
        verify(oldTx).updateDetails("Nova Desc", new BigDecimal("20.00"), now, TransactionType.EXPENSE, categoryId, null);
        verify(accountRepo).updateBalanceAtomic(accountId, new BigDecimal("-20.00")); // Aplica despesa nova (-20)
        
        // Verifica processamento dos novos efeitos
        verify(transactionRepo).save(oldTx);
        verify(processTransactionInGoal).execute(oldTx);
        verify(processTransactionInBudget).execute(oldTx);
    }
}
