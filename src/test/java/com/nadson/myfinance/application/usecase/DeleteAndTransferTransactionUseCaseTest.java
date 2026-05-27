package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.ProcessTransactionInGoalPort;
import com.nadson.myfinance.application.port.in.RevertTransactionInGoalPort;
import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
import com.nadson.myfinance.application.port.out.BudgetRepositoryPort;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteAndTransferTransactionUseCaseTest {

    @Mock private TransactionRepositoryPort txRepo;
    @Mock private AccountRepositoryPort accRepo;
    @Mock private BudgetRepositoryPort budgetRepo;
    @Mock private RevertTransactionInGoalPort revertGoal;
    @Mock private ProcessTransactionInGoalPort processGoal;

    @Test
    @DisplayName("DeleteTransaction: Should revert balance and delete")
    void shouldDeleteTransactionAndRevertBalance() {
        DeleteTransactionUseCase useCase = new DeleteTransactionUseCase(txRepo, accRepo, budgetRepo, revertGoal);

        UUID txId = UUID.randomUUID();
        UUID accId = UUID.randomUUID();
        Transaction tx = new Transaction(txId, "Tx", new BigDecimal("50.00"), LocalDateTime.now(), TransactionType.EXPENSE, accId, null, false, null, null, TransactionStatus.COMPLETED, null);
        Account account = mock(Account.class);

        when(txRepo.findById(txId)).thenReturn(tx);
        when(accRepo.findById(accId)).thenReturn(account);

        useCase.execute(txId);

        verify(account).deposit(new BigDecimal("50.00")); // Despesa deletada vira depósito
        verify(accRepo).save(account);
        verify(revertGoal).execute(tx);
        verify(txRepo).deleteById(txId);
    }

    @Test
    @DisplayName("TransferUseCase: Should create double entry and update balances")
    void shouldCreateTransfer() {
        TransferUseCase useCase = new TransferUseCase(accRepo, txRepo, processGoal);

        UUID senderId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("300.00");

        when(accRepo.findById(senderId)).thenReturn(new Account(senderId, UUID.randomUUID(), AccountType.CHECKING, "Sender", BigDecimal.ZERO));
        when(accRepo.findById(receiverId)).thenReturn(new Account(receiverId, UUID.randomUUID(), AccountType.CHECKING, "Receiver", BigDecimal.ZERO));

        useCase.execute(senderId, receiverId, amount, LocalDateTime.now(), "Transfer", null, null);

        // Sender perde saldo, Receiver ganha saldo
        verify(accRepo).updateBalanceAtomic(senderId, new BigDecimal("-300.00"));
        verify(accRepo).updateBalanceAtomic(receiverId, new BigDecimal("300.00"));

        // Salva duas transações
        verify(txRepo, times(2)).save(any(Transaction.class));
        verify(processGoal, times(2)).execute(any(Transaction.class));
    }
}