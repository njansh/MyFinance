package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.out.*;
import com.nadson.myfinance.domain.entity.*;
import com.nadson.myfinance.domain.enums.AccountType;
import com.nadson.myfinance.domain.enums.TransactionType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateTransactionUseCaseTest {

    @Mock private TransactionRepositoryPort transactionRepo;
    @Mock private AccountRepositoryPort accountRepo;
    @Mock private CategoryRepositoryPort categoryRepo;

    @InjectMocks private UpdateTransactionUseCase useCase;

    @Test
    void shouldRecalculateBalanceWhenUpdatingTransactionAmount() {
        UUID txId = UUID.randomUUID();
        UUID accId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        Account account = new Account(accId, UUID.randomUUID(), AccountType.CHECKING, "Conta Teste", new BigDecimal("1000.00"));
        Transaction oldTx = new Transaction(txId, "Antigo", new BigDecimal("50.00"), now, TransactionType.EXPENSE, accId, UUID.randomUUID(), false, null, null);

        when(transactionRepo.findById(txId)).thenReturn(oldTx);

        useCase.execute(txId, "Novo", new BigDecimal("150.00"), now, TransactionType.EXPENSE, accId, UUID.randomUUID());

        verify(accountRepo).updateBalanceAtomic(accId, new BigDecimal("50.00")); // Estorno
        verify(accountRepo).updateBalanceAtomic(accId, new BigDecimal("-150.00")); // Novo débito
        verify(transactionRepo).save(any(Transaction.class));
    }
}