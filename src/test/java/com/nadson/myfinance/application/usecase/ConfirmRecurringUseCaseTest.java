package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.ProcessTransactionInGoalPort;
import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.domain.enums.AccountType;
import com.nadson.myfinance.domain.enums.TransactionStatus;
import com.nadson.myfinance.domain.enums.TransactionType;
import com.nadson.myfinance.domain.exception.BusinessRuleException;
import com.nadson.myfinance.domain.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ConfirmRecurringUseCaseTest {

    private TransactionRepositoryPort transactionRepository;
    private AccountRepositoryPort accountRepository;
    private ProcessTransactionInGoalPort processTransactionInGoal;
    private ConfirmRecurringUseCase useCase;

    @BeforeEach
    void setUp() {
        transactionRepository = mock(TransactionRepositoryPort.class);
        accountRepository = mock(AccountRepositoryPort.class);
        processTransactionInGoal = mock(ProcessTransactionInGoalPort.class);
        useCase = new ConfirmRecurringUseCase(transactionRepository, accountRepository, processTransactionInGoal);
    }

    @Test
    @DisplayName("Deve confirmar transação de receita com sucesso")
    void shouldConfirmIncomeSuccessfully() {
        UUID userId = UUID.randomUUID();
        UUID txId = UUID.randomUUID();
        UUID accId = UUID.randomUUID();

        Account account = spy(new Account(accId, userId, AccountType.CHECKING, "Conta", BigDecimal.ZERO));
        Transaction tx = spy(new Transaction(txId, "Desc", BigDecimal.TEN, LocalDateTime.now(), TransactionType.INCOME, accId, UUID.randomUUID(), false, null, null, TransactionStatus.PENDING, null));

        when(transactionRepository.findById(txId)).thenReturn(tx);
        when(accountRepository.findById(accId)).thenReturn(account);
        when(transactionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        useCase.execute(userId, txId, new BigDecimal("100.00"), LocalDateTime.now());

        verify(account).deposit(new BigDecimal("100.00"));
        verify(accountRepository).save(account);
        verify(tx).markAsCompleted();
        verify(processTransactionInGoal).execute(any());
    }

    @Test
    @DisplayName("Deve falhar se a transação já estiver concluída")
    void shouldFailWhenAlreadyCompleted() {
        UUID txId = UUID.randomUUID();
        Transaction tx = mock(Transaction.class);
        when(tx.getStatus()).thenReturn(TransactionStatus.COMPLETED);
        when(transactionRepository.findById(txId)).thenReturn(tx);

        assertThrows(BusinessRuleException.class, () -> useCase.execute(UUID.randomUUID(), txId, BigDecimal.TEN, LocalDateTime.now()));
    }
    @Test
    @DisplayName("Deve confirmar transação de despesa (saída) com sucesso")
    void shouldConfirmExpenseSuccessfully() {
        UUID userId = UUID.randomUUID();
        UUID txId = UUID.randomUUID();
        UUID accId = UUID.randomUUID();

        Account account = spy(new Account(accId, userId, AccountType.CHECKING, "Conta", new BigDecimal("500.00")));
        Transaction tx = spy(new Transaction(txId, "Desc", BigDecimal.TEN, LocalDateTime.now(), TransactionType.EXPENSE, accId, UUID.randomUUID(), false, null, null, TransactionStatus.PENDING, null));

        when(transactionRepository.findById(txId)).thenReturn(tx);
        when(accountRepository.findById(accId)).thenReturn(account);
        when(transactionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        useCase.execute(userId, txId, new BigDecimal("100.00"), LocalDateTime.now());

        verify(account).withdraw(new BigDecimal("100.00")); // Garante a execução do else
        verify(accountRepository).save(account);
    }
    @Test
    @DisplayName("Deve falhar se a transação pertencer a outro usuário")
    void shouldFailWhenUserDoesNotOwnAccount() {
        UUID userId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        UUID txId = UUID.randomUUID();
        UUID accId = UUID.randomUUID();

        Transaction tx = new Transaction(txId, "Desc", BigDecimal.TEN, LocalDateTime.now(), TransactionType.INCOME, accId, UUID.randomUUID(), false, null, null, TransactionStatus.PENDING, null);
        Account account = new Account(accId, otherUserId, AccountType.CHECKING, "Conta", BigDecimal.ZERO);

        when(transactionRepository.findById(txId)).thenReturn(tx);
        when(accountRepository.findById(accId)).thenReturn(account);

        assertThrows(BusinessRuleException.class, () -> useCase.execute(userId, txId, BigDecimal.TEN, LocalDateTime.now()));
    }
    @Test
    @DisplayName("Deve falhar quando a transação não é encontrada")
    void shouldFailWhenTransactionNotFound() {
        UUID txId = UUID.randomUUID();

        // Simula que o repositório não encontrou a transação
        when(transactionRepository.findById(txId)).thenReturn(null);

        // Verifica se a exceção correta é lançada
        assertThrows(ResourceNotFoundException.class, () ->
                useCase.execute(UUID.randomUUID(), txId, BigDecimal.TEN, LocalDateTime.now()));
    }
}