package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.domain.enums.AccountType;
import com.nadson.myfinance.domain.enums.TransactionType;
import com.nadson.myfinance.domain.exception.AccountNotFoundException;
import com.nadson.myfinance.infrastructure.adapter.web.dto.response.BalanceResponse;
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
class GetAccountBalanceUseCaseTest {

    @Mock
    private TransactionRepositoryPort transactionRepositoryPort;

    @Mock
    private AccountRepositoryPort accountRepositoryPort;

    @InjectMocks
    private GetAccountBalanceUseCase useCase;

    @Test
    @DisplayName("Deve calcular totais de receitas e despesas corretamente para um período")
    void shouldCalculateBalanceCorrectlyForPeriod() {
        UUID accountId = UUID.randomUUID();
        LocalDateTime start = LocalDateTime.now().minusDays(30);
        LocalDateTime end = LocalDateTime.now();

        Account account = new Account(accountId, UUID.randomUUID(), AccountType.CHECKING, "Inter", new BigDecimal("1000.00"));

        List<Transaction> transactions = List.of(
                new Transaction(UUID.randomUUID(), "Salário", new BigDecimal("3000.00"), LocalDateTime.now(), TransactionType.INCOME, accountId, null, false, null, null),
                new Transaction(UUID.randomUUID(), "Freelance", new BigDecimal("500.00"), LocalDateTime.now(), TransactionType.INCOME, accountId, null, false, null, null),
                new Transaction(UUID.randomUUID(), "Aluguel", new BigDecimal("1200.00"), LocalDateTime.now(), TransactionType.EXPENSE, accountId, null, false, null, null),
                new Transaction(UUID.randomUUID(), "Internet", new BigDecimal("100.00"), LocalDateTime.now(), TransactionType.EXPENSE, accountId, null, false, null, null)
        );

        when(accountRepositoryPort.findById(accountId)).thenReturn(account);
        when(transactionRepositoryPort.findAllByAccountIdAndDateBetween(accountId, start, end)).thenReturn(transactions);

        BalanceResponse result = useCase.execute(accountId, start, end);

        assertNotNull(result);
        assertEquals(new BigDecimal("3500.00"), result.totalIncomes());
        assertEquals(new BigDecimal("1300.00"), result.totalExpenses());
        assertEquals(new BigDecimal("1000.00"), result.balance());

        verify(transactionRepositoryPort).findAllByAccountIdAndDateBetween(accountId, start, end);
        verify(transactionRepositoryPort, never()).findAllByAccountId(accountId);
    }

    @Test
    @DisplayName("Deve buscar todas as transações quando as datas não são informadas")
    void shouldFetchAllTransactionsWhenDatesAreNull() {
        UUID accountId = UUID.randomUUID();
        Account account = new Account(accountId, UUID.randomUUID(), AccountType.CHECKING, "Nubank", BigDecimal.ZERO);

        when(accountRepositoryPort.findById(accountId)).thenReturn(account);
        when(transactionRepositoryPort.findAllByAccountId(accountId)).thenReturn(List.of());

        useCase.execute(accountId, null, null);

        verify(transactionRepositoryPort).findAllByAccountId(accountId);
        verify(transactionRepositoryPort, never()).findAllByAccountIdAndDateBetween(any(), any(), any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando a conta não for encontrada")
    void shouldThrowExceptionWhenAccountNotFound() {
        UUID accountId = UUID.randomUUID();
        when(accountRepositoryPort.findById(accountId)).thenReturn(null);

        assertThrows(AccountNotFoundException.class, () ->
                useCase.execute(accountId, null, null));

        verifyNoInteractions(transactionRepositoryPort);
    }
}