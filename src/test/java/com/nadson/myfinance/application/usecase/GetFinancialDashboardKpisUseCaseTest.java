package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.out.*;
import com.nadson.myfinance.domain.entity.*;
import com.nadson.myfinance.domain.enums.AccountType;
import com.nadson.myfinance.domain.enums.TransactionType;
import com.nadson.myfinance.infrastructure.adapter.web.dto.response.KpiDashboardResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetFinancialDashboardKpisUseCaseTest {

    @Mock private AccountRepositoryPort accountRepository;
    @Mock private TransactionRepositoryPort transactionRepository;
    @Mock private RecurringTemplateRepositoryPort recurringTemplateRepository;

    @InjectMocks
    private GetFinancialDashboardKpisUseCase useCase;

    @Test
    @DisplayName("Deve retornar zeros se o usuário não possuir contas")
    void shouldReturnZerosWhenNoAccountsFound() {
        when(accountRepository.findByUserId(any())).thenReturn(List.of());

        KpiDashboardResponse response = useCase.execute(UUID.randomUUID(), 5, 2026);

        assertThat(response.netWorth()).isEqualByComparingTo("0");
    }

    @Test
    @DisplayName("Deve calcular KPIs corretamente para um mês específico")
    void shouldCalculateKpisForSpecificMonth() {
        UUID userId = UUID.randomUUID();
        Account acc = new Account(UUID.randomUUID(), userId, AccountType.CHECKING, "Acc", new BigDecimal("1000.00"));

        when(accountRepository.findByUserId(userId)).thenReturn(List.of(acc));
        // Mock somas históricas e mensais
        when(transactionRepository.sumBalanceBeforeDate(anyList(), any(), eq(TransactionType.INCOME))).thenReturn(new BigDecimal("500.00"));
        when(transactionRepository.sumBalanceBeforeDate(anyList(), any(), eq(TransactionType.EXPENSE))).thenReturn(new BigDecimal("200.00"));
        when(transactionRepository.sumTransactionsByAccountsAndPeriod(anyList(), any(), any(), eq(TransactionType.INCOME))).thenReturn(new BigDecimal("100.00"));
        when(transactionRepository.sumTransactionsByAccountsAndPeriod(anyList(), any(), any(), eq(TransactionType.EXPENSE))).thenReturn(new BigDecimal("50.00"));

        KpiDashboardResponse response = useCase.execute(userId, 5, 2026);

        assertThat(response.lastMonthBalance()).isEqualByComparingTo("300.00"); // 500 - 200
        assertThat(response.monthlyIncome()).isEqualByComparingTo("100.00");
    }

    @Test
    @DisplayName("Deve incluir recorrências no forecast do próximo mês")
    void shouldIncludeRecurrencesInForecast() {
        UUID userId = UUID.randomUUID();
        Account acc = new Account(UUID.randomUUID(), userId, AccountType.CHECKING, "Acc", new BigDecimal("1000.00"));

        RecurringTemplate rec = mock(RecurringTemplate.class);
        when(rec.getType()).thenReturn(TransactionType.INCOME);
        when(rec.getExpectedAmount()).thenReturn(new BigDecimal("200.00"));

        when(accountRepository.findByUserId(userId)).thenReturn(List.of(acc));
        when(recurringTemplateRepository.findActiveByUserId(userId)).thenReturn(List.of(rec));

        KpiDashboardResponse response = useCase.execute(userId, null, null);

        assertThat(response.nextMonthForecast()).isEqualByComparingTo("1200.00"); // 1000 + 200
    }

}