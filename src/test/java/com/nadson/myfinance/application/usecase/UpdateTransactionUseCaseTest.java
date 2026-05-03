package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
import com.nadson.myfinance.application.port.out.CategoryRepositoryPort;
import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateTransactionUseCaseTest {

    @Mock private TransactionRepositoryPort transactionRepo;
    @Mock private CategoryRepositoryPort categoryRepo;
    @Mock private AccountRepositoryPort accountRepo;

    @InjectMocks
    private UpdateTransactionUseCase useCase;

    @Test
    @DisplayName("Deve atualizar transação estornando valor antigo e aplicando novo saldo")
    void shouldUpdateTransactionSuccessfully() {
        // Arrange
        UUID txId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        BigDecimal oldAmount = new BigDecimal("50.00");
        BigDecimal newAmount = new BigDecimal("70.00");
        LocalDateTime now = LocalDateTime.now();

        // Transação antiga era uma despesa de 50.00
        Transaction oldTx = new Transaction(txId, "Antiga", oldAmount, now, TransactionType.EXPENSE, accountId, null, false, null, null);

        when(transactionRepo.findById(txId)).thenReturn(oldTx);

        // Act
        useCase.execute(txId, "Nova Descrição", newAmount, now, TransactionType.EXPENSE, accountId, null);

        // Assert
        // 1. Estorno: Se era despesa de 50, deve somar 50 para estornar
        verify(accountRepo).updateBalanceAtomic(accountId, oldAmount);

        // 2. Novo ajuste: Se é nova despesa de 70, deve subtrair 70
        verify(accountRepo).updateBalanceAtomic(accountId, newAmount.negate());

        verify(transactionRepo).save(oldTx);
    }

    @Test
    @DisplayName("Deve permitir trocar o tipo de transação de despesa para receita")
    void shouldUpdateAndChangeTypeSuccessfully() {
        UUID txId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();

        // Antiga: Despesa de 100.00
        Transaction oldTx = new Transaction(txId, "Antiga", new BigDecimal("100.00"), LocalDateTime.now(), TransactionType.EXPENSE, accountId, null, false, null, null);

        when(transactionRepo.findById(txId)).thenReturn(oldTx);

        // Nova: Receita de 150.00
        useCase.execute(txId, "Nova", new BigDecimal("150.00"), LocalDateTime.now(), TransactionType.INCOME, accountId, null);

        // Estorno da despesa (+100)
        verify(accountRepo).updateBalanceAtomic(accountId, new BigDecimal("100.00"));
        // Aplicação da receita (+150)
        verify(accountRepo).updateBalanceAtomic(accountId, new BigDecimal("150.00"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando a transação não existir")
    void shouldThrowExceptionWhenNotFound() {
        UUID txId = UUID.randomUUID();
        when(transactionRepo.findById(txId)).thenReturn(null);

        assertThrows(TransactionNotFoundException.class, () ->
                useCase.execute(txId, "Desc", BigDecimal.ONE, LocalDateTime.now(), TransactionType.INCOME, UUID.randomUUID(), null));

        verifyNoInteractions(accountRepo);
    }
}