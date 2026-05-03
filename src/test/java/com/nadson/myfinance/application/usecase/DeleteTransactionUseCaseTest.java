package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.domain.enums.AccountType;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteTransactionUseCaseTest {

    @Mock private TransactionRepositoryPort transactionRepository;
    @Mock private AccountRepositoryPort accountRepository;

    @InjectMocks
    private DeleteTransactionUseCase useCase;

    @Test
    @DisplayName("Deve deletar transação simples e estornar o saldo da conta")
    void shouldDeleteSimpleTransactionAndReverseBalance() {
        UUID transactionId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("100.00");

        Transaction transaction = new Transaction(transactionId, "Lanche", amount, LocalDateTime.now(),
                TransactionType.EXPENSE, accountId, UUID.randomUUID(), false, null, null);

        Account account = new Account(accountId, UUID.randomUUID(), AccountType.CHECKING, "Inter", new BigDecimal("500.00"));

        when(transactionRepository.findById(transactionId)).thenReturn(transaction);
        when(accountRepository.findById(accountId)).thenReturn(account);

        useCase.execute(transactionId);

        assertEquals(new BigDecimal("600.00"), account.getBalance());
        verify(accountRepository).save(account);
        verify(transactionRepository).deleteById(transactionId);
    }

    @Test
    @DisplayName("Deve deletar ambas as transações de uma transferência e estornar saldos")
    void shouldDeleteTransferTransactionsAndReverseBothBalances() {
        UUID transferId = UUID.randomUUID();
        UUID t1Id = UUID.randomUUID();
        UUID t2Id = UUID.randomUUID();
        UUID accOrigemId = UUID.randomUUID();
        UUID accDestinoId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("50.00");


        Transaction t1 = new Transaction(t1Id, "Transferência", amount, LocalDateTime.now(),
                TransactionType.EXPENSE, accOrigemId, null, true, transferId, null);

        Transaction t2 = new Transaction(t2Id, "Transferência", amount, LocalDateTime.now(),
                TransactionType.INCOME, accDestinoId, null, true, transferId, null);

        Account accOrigem = new Account(accOrigemId, UUID.randomUUID(), AccountType.CHECKING, "Origem", new BigDecimal("100.00"));
        Account accDestino = new Account(accDestinoId, UUID.randomUUID(), AccountType.CHECKING, "Destino", new BigDecimal("100.00"));

        when(transactionRepository.findById(t1Id)).thenReturn(t1);
        when(transactionRepository.findAllByTransferID(transferId)).thenReturn(List.of(t1, t2));
        when(accountRepository.findById(accOrigemId)).thenReturn(accOrigem);
        when(accountRepository.findById(accDestinoId)).thenReturn(accDestino);

        useCase.execute(t1Id);

        assertEquals(new BigDecimal("150.00"), accOrigem.getBalance());
        assertEquals(new BigDecimal("50.00"), accDestino.getBalance());

        verify(transactionRepository).deleteById(t1Id);
        verify(transactionRepository).deleteById(t2Id);
        verify(accountRepository, times(2)).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando transação não existe")
    void shouldThrowExceptionWhenTransactionNotFound() {
        UUID id = UUID.randomUUID();
        when(transactionRepository.findById(id)).thenReturn(null);

        assertThrows(TransactionNotFoundException.class, () -> useCase.execute(id));
        verifyNoInteractions(accountRepository);
    }
}