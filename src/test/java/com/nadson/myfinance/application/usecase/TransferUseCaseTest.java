package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.enums.AccountType;
import com.nadson.myfinance.domain.exception.AccountNotFoundException;
import com.nadson.myfinance.domain.exception.BusinessRuleException;
import com.nadson.myfinance.domain.exception.InvalidTransactionValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferUseCaseTest {

    @Mock private AccountRepositoryPort accountRepositoryPort;
    @Mock private TransactionRepositoryPort transactionRepositoryPort;

    @InjectMocks
    private TransferUseCase useCase;

    @Test
    @DisplayName("Deve realizar transferência com sucesso entre duas contas distintas")
    void shouldTransferSuccessfully() {
        UUID senderId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("100.00");
        LocalDateTime now = LocalDateTime.now();

        Account sender = new Account(senderId, UUID.randomUUID(), AccountType.CHECKING, "Conta Origem", new BigDecimal("500.00"));
        Account receiver = new Account(receiverId, UUID.randomUUID(), AccountType.CHECKING, "Conta Destino", new BigDecimal("200.00"));

        when(accountRepositoryPort.findById(senderId)).thenReturn(sender);
        when(accountRepositoryPort.findById(receiverId)).thenReturn(receiver);

        useCase.execute(senderId, receiverId, amount, now, "Pix para amigo", null, null);

        verify(accountRepositoryPort).updateBalanceAtomic(senderId, amount.negate());
        verify(accountRepositoryPort).updateBalanceAtomic(receiverId, amount);

        verify(transactionRepositoryPort, times(2)).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar transferir valor zero ou negativo")
    void shouldThrowExceptionForInvalidAmount() {
        assertThrows(InvalidTransactionValueException.class, () ->
                useCase.execute(UUID.randomUUID(), UUID.randomUUID(), BigDecimal.ZERO, LocalDateTime.now(), null, null, null));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar transferir para a mesma conta")
    void shouldThrowExceptionForSameAccount() {
        UUID accountId = UUID.randomUUID();
        Account acc = new Account(accountId, UUID.randomUUID(), AccountType.CHECKING, "Mesma Conta", BigDecimal.TEN);

        when(accountRepositoryPort.findById(accountId)).thenReturn(acc);

        assertThrows(BusinessRuleException.class, () ->
                useCase.execute(accountId, accountId, BigDecimal.ONE, LocalDateTime.now(), null, null, null));
    }

    @Test
    @DisplayName("Deve lançar exceção se a conta de destino não for encontrada")
    void shouldThrowExceptionWhenReceiverNotFound() {
        UUID senderId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();

        when(accountRepositoryPort.findById(senderId)).thenReturn(mock(Account.class));
        when(accountRepositoryPort.findById(receiverId)).thenReturn(null);

        assertThrows(AccountNotFoundException.class, () ->
                useCase.execute(senderId, receiverId, BigDecimal.TEN, LocalDateTime.now(), null, null, null));
    }
}