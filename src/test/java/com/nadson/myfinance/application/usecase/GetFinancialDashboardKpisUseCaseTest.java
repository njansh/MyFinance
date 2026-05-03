package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.out.*;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.enums.AccountType;
import com.nadson.myfinance.infrastructure.adapter.web.dto.response.KpiDashboardResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetFinancialDashboardKpisUseCaseTest {

    @Mock private AccountRepositoryPort accountRepo;
    @Mock private TransactionRepositoryPort transactionRepo;
    @InjectMocks private GetFinancialDashboardKpisUseCase useCase;

    @Test
    void shouldCalculateFinancialKpisCorrectly() {
        UUID userId = UUID.randomUUID();
        Account acc = new Account(UUID.randomUUID(), userId, AccountType.CHECKING, "Main", new BigDecimal("500.00"));

        when(accountRepo.findByUserId(userId)).thenReturn(List.of(acc));
        when(transactionRepo.sumTransactionsByAccountsAndPeriod(any(), any(), any(), any()))
                .thenReturn(new BigDecimal("1000.00"), new BigDecimal("500.00"));

        KpiDashboardResponse result = useCase.execute(userId, 5, 2026);

        assertEquals(new BigDecimal("500.00"), result.netWorth());
        assertEquals(new BigDecimal("500.00"), result.cashFlow());
    }
}
