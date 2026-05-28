package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.ProcessTransactionInGoalPort;
import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.domain.enums.TransactionStatus;
import com.nadson.myfinance.domain.enums.TransactionType;
import com.nadson.myfinance.domain.exception.AccountNotFoundException;
import com.nadson.myfinance.domain.exception.BusinessRuleException;
import com.nadson.myfinance.domain.exception.InvalidTransactionValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferUseCaseTest {

    @Mock private AccountRepositoryPort accountRepositoryPort;
    @Mock private TransactionRepositoryPort transactionRepositoryPort;
    @Mock private ProcessTransactionInGoalPort processTransactionInGoal;

    @InjectMocks
    private TransferUseCase useCase;

    @Test
    @DisplayName("Deve falhar se o valor da transferência for menor ou igual a zero")
    void shouldFailWhenAmountIsLessThanOrEqualToZero() {
        UUID sender = UUID.randomUUID();
        UUID receiver = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        assertThrows(InvalidTransactionValueException.class, () ->
                useCase.execute(sender, receiver, BigDecimal.ZERO, now, "Pix", null, null));

        assertThrows(InvalidTransactionValueException.class, () ->
                useCase.execute(sender, receiver, new BigDecimal("-10.00"), now, "Pix", null, null));
    }

    @Test
    @DisplayName("Deve falhar se a conta de origem não for encontrada")
    void shouldFailWhenSenderAccountNotFound() {
        UUID sender = UUID.randomUUID();
        UUID receiver = UUID.randomUUID();
        when(accountRepositoryPort.findById(sender)).thenReturn(null);

        assertThrows(AccountNotFoundException.class, () ->
                useCase.execute(sender, receiver, BigDecimal.TEN, LocalDateTime.now(), "Pix", null, null));
    }

    @Test
    @DisplayName("Deve falhar se a conta de destino não for encontrada")
    void shouldFailWhenReceiverAccountNotFound() {
        UUID sender = UUID.randomUUID();
        UUID receiver = UUID.randomUUID();
        when(accountRepositoryPort.findById(sender)).thenReturn(mock(Account.class));
        when(accountRepositoryPort.findById(receiver)).thenReturn(null);

        assertThrows(AccountNotFoundException.class, () ->
                useCase.execute(sender, receiver, BigDecimal.TEN, LocalDateTime.now(), "Pix", null, null));
    }

    @Test
    @DisplayName("Deve falhar se as contas de origem e destino forem iguais")
    void shouldFailWhenSenderAndReceiverAreTheSame() {
        UUID accountId = UUID.randomUUID();
        when(accountRepositoryPort.findById(accountId)).thenReturn(mock(Account.class));

        assertThrows(BusinessRuleException.class, () ->
                useCase.execute(accountId, accountId, BigDecimal.TEN, LocalDateTime.now(), "Pix", null, null));
    }

    @Test
    @DisplayName("Deve transferir com sucesso aplicando saldo remanescente na conta de origem e usando descrição customizada")
    void shouldTransferSuccessfullyMappingSenderBalanceAndCustomDescription() {
        UUID sender = UUID.randomUUID();
        UUID receiver = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        BigDecimal amount = new BigDecimal("50.00");
        BigDecimal sourceBalanceAfter = new BigDecimal("450.00");

        when(accountRepositoryPort.findById(sender)).thenReturn(mock(Account.class));
        when(accountRepositoryPort.findById(receiver)).thenReturn(mock(Account.class));

        useCase.execute(sender, receiver, amount, now, "Pagamento Aluguel", sender, sourceBalanceAfter);

        // Captura as duas transações salvas (Débito e Crédito)
        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepositoryPort, times(2)).save(transactionCaptor.capture());
        List<Transaction> savedTransactions = transactionCaptor.getAllValues();

        Transaction debit = savedTransactions.get(0);
        Transaction credit = savedTransactions.get(1);

        // Asserts do Débito (Origem)
        assertThat(debit.getType()).isEqualTo(TransactionType.EXPENSE);
        assertThat(debit.getDescription()).isEqualTo("Pagamento Aluguel");
        assertThat(debit.getAccountBalanceAfter()).isEqualTo(sourceBalanceAfter);

        // Asserts do Crédito (Destino)
        assertThat(credit.getType()).isEqualTo(TransactionType.INCOME);
        assertThat(credit.getAccountBalanceAfter()).isNull();

        // Verifica atualizações atômicas e processamento de metas
        verify(accountRepositoryPort).updateBalanceAtomic(sender, amount.negate());
        verify(accountRepositoryPort).updateBalanceAtomic(receiver, amount);
        verify(processTransactionInGoal).execute(debit);
        verify(processTransactionInGoal).execute(credit);
    }

    @Test
    @DisplayName("Deve transferir com sucesso aplicando saldo na conta de destino e usando descrição padrão se for vazia ou nula")
    void shouldTransferSuccessfullyMappingReceiverBalanceAndDefaultDescription() {
        UUID sender = UUID.randomUUID();
        UUID receiver = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        BigDecimal amount = new BigDecimal("100.00");
        BigDecimal sourceBalanceAfter = new BigDecimal("1100.00");

        when(accountRepositoryPort.findById(sender)).thenReturn(mock(Account.class));
        when(accountRepositoryPort.findById(receiver)).thenReturn(mock(Account.class));

        // Testando descrição com string em branco para forçar o "Transferência" padrão
        useCase.execute(sender, receiver, amount, now, "   ", receiver, sourceBalanceAfter);

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepositoryPort, times(2)).save(transactionCaptor.capture());

        Transaction debit = transactionCaptor.getAllValues().get(0);
        Transaction credit = transactionCaptor.getAllValues().get(1);

        assertThat(debit.getDescription()).isEqualTo("Transferência");
        assertThat(debit.getAccountBalanceAfter()).isNull();

        assertThat(credit.getDescription()).isEqualTo("Transferência");
        assertThat(credit.getAccountBalanceAfter()).isEqualTo(sourceBalanceAfter);
    }

    @Test
    @DisplayName("Deve transferir com sucesso quando a conta de origem do saldo não for informada")
    void shouldTransferSuccessfullyWhenSourceAccountIdIsNull() {
        UUID sender = UUID.randomUUID();
        UUID receiver = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        when(accountRepositoryPort.findById(sender)).thenReturn(mock(Account.class));
        when(accountRepositoryPort.findById(receiver)).thenReturn(mock(Account.class));

        // description = null e sourceAccountId = null para matar os short-circuits de nulidade do evaluation
        useCase.execute(sender, receiver, BigDecimal.TEN, now, null, null, null);

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepositoryPort, times(2)).save(transactionCaptor.capture());

        assertThat(transactionCaptor.getAllValues().get(0).getAccountBalanceAfter()).isNull();
        assertThat(transactionCaptor.getAllValues().get(1).getAccountBalanceAfter()).isNull();
    }
}