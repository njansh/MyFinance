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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAccountBalanceUseCaseTest {

    @Mock private TransactionRepositoryPort transactionRepositoryPort;
    @Mock private AccountRepositoryPort accountRepositoryPort;

    @InjectMocks
    private GetAccountBalanceUseCase useCase;

    @Test
    @DisplayName("Deve calcular saldo e totais sem intervalo de datas")
    void shouldGetBalanceWithoutDateRange() {
        UUID accId = UUID.randomUUID();
        Account account = new Account(accId, UUID.randomUUID(), AccountType.CHECKING, "Acc", new BigDecimal("500.00"));

        Transaction income = mock(Transaction.class);
        when(income.getType()).thenReturn(TransactionType.INCOME);
        when(income.getAmount()).thenReturn(new BigDecimal("1000.00"));

        Transaction expense = mock(Transaction.class);
        when(expense.getType()).thenReturn(TransactionType.EXPENSE);
        when(expense.getAmount()).thenReturn(new BigDecimal("200.00"));

        when(accountRepositoryPort.findById(accId)).thenReturn(account);
        when(transactionRepositoryPort.findAllByAccountId(accId)).thenReturn(List.of(income, expense));

        BalanceResponse response = useCase.execute(accId, null, null);

        assertEquals(new BigDecimal("1000.00"), response.getIncomes());
        assertEquals(new BigDecimal("200.00"), response.getExpenses());
        assertEquals(new BigDecimal("500.00"), response.balance());
    }

    @Test
    @DisplayName("Deve calcular saldo usando intervalo de datas")
    void shouldGetBalanceWithDateRange() {
        UUID accId = UUID.randomUUID();
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        Account account = new Account(accId, UUID.randomUUID(), AccountType.CHECKING, "Acc", BigDecimal.ZERO);

        when(accountRepositoryPort.findById(accId)).thenReturn(account);
        when(transactionRepositoryPort.findAllByAccountIdAndDateBetween(accId, start, end)).thenReturn(List.of());

        useCase.execute(accId, start, end);

        verify(transactionRepositoryPort).findAllByAccountIdAndDateBetween(accId, start, end);
    }

    @Test
    @DisplayName("Deve falhar se a conta não for encontrada")
    void shouldFailWhenAccountNotFound() {
        when(accountRepositoryPort.findById(any())).thenReturn(null);
        assertThrows(AccountNotFoundException.class, () -> useCase.execute(UUID.randomUUID(), null, null));
    }
}