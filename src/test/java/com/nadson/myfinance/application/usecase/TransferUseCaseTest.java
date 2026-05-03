package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.enums.AccountType;
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
class TransferUseCaseTest {

    @Mock private AccountRepositoryPort accountRepo;
    @Mock private TransactionRepositoryPort transactionRepo;

    @InjectMocks private TransferUseCase useCase;

    @Test
    void shouldExecuteTransferCreatingTwoMirrorTransactions() {
        UUID userId = UUID.randomUUID();
        UUID fromId = UUID.randomUUID();
        UUID toId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("100.00");

        Account fromAcc = new Account(fromId, userId, AccountType.CHECKING, "Origem", new BigDecimal("500.00"));
        Account toAcc = new Account(toId, userId, AccountType.INVESTMENT, "Destino", new BigDecimal("0.00"));

        when(accountRepo.findById(fromId)).thenReturn(fromAcc);
        when(accountRepo.findById(toId)).thenReturn(toAcc);

        useCase.execute(fromId, toId, amount, LocalDateTime.now(), "Reserva", null, null);


        verify(accountRepo).updateBalanceAtomic(fromId, amount.negate());
        verify(accountRepo).updateBalanceAtomic(toId, amount);

        verify(transactionRepo, times(2)).save(any());
    }
}