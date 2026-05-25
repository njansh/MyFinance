//package com.nadson.myfinance.application.usecase;
//
//import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
//import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
//import com.nadson.myfinance.domain.entity.Account;
//import com.nadson.myfinance.domain.enums.AccountType;
//import com.nadson.myfinance.domain.enums.TransactionType;
//import com.nadson.myfinance.infrastructure.adapter.web.dto.response.KpiDashboardResponse;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class GetFinancialDashboardKpisUseCaseTest {
//
//    @Mock
//    private AccountRepositoryPort accountRepository;
//
//    @Mock
//    private TransactionRepositoryPort transactionRepository;
//
//    @InjectMocks
//    private GetFinancialDashboardKpisUseCase useCase;
//
//    @Test
//    @DisplayName("Deve calcular KPIs do dashboard com sucesso para um usuário com investimentos")
//    void shouldCalculateDashboardKpisSuccessfully() {
//        UUID userId = UUID.randomUUID();
//        UUID checkingId = UUID.randomUUID();
//        UUID investmentId = UUID.randomUUID();
//
//        Account checking = new Account(checkingId, userId, AccountType.CHECKING, "Inter", new BigDecimal("1000.00"));
//        Account investment = new Account(investmentId, userId, AccountType.INVESTMENT, "Tesouro", new BigDecimal("5000.00"));
//
//        when(accountRepository.findByUserId(userId)).thenReturn(List.of(checking, investment));
//
//        when(transactionRepository.sumTransactionsByAccountsAndPeriod(anyList(), any(), any(), eq(TransactionType.INCOME)))
//                .thenReturn(new BigDecimal("4000.00"));
//        when(transactionRepository.sumTransactionsByAccountsAndPeriod(anyList(), any(), any(), eq(TransactionType.EXPENSE)))
//                .thenReturn(new BigDecimal("2500.00"));
//        when(transactionRepository.sumSavingsByAccountsAndPeriod(anyList(), any(), any()))
//                .thenReturn(new BigDecimal("800.00"));
//
//        KpiDashboardResponse result = useCase.execute(userId, 5, 2026);
//
//        assertNotNull(result);
//        assertEquals(new BigDecimal("6000.00"), result.netWorth()); // 1000 + 5000
//        assertEquals(new BigDecimal("4000.00"), result.monthlyIncome());
//        assertEquals(new BigDecimal("2500.00"), result.monthlyExpense());
//        assertEquals(new BigDecimal("1500.00"), result.cashFlow()); // 4000 - 2500
//        assertEquals(new BigDecimal("20.0000"), result.savingsRatio()); // (800 / 4000) * 100
//    }
//
//    @Test
//    @DisplayName("Deve retornar KPIs zerados quando o usuário não possui contas")
//    void shouldReturnZeroKpisWhenUserHasNoAccounts() {
//        UUID userId = UUID.randomUUID();
//        when(accountRepository.findByUserId(userId)).thenReturn(List.of());
//
//        KpiDashboardResponse result = useCase.execute(userId, 5, 2026);
//
//        assertEquals(BigDecimal.ZERO, result.netWorth());
//        assertEquals(BigDecimal.ZERO, result.monthlyIncome());
//        assertEquals(BigDecimal.ZERO, result.savingsRatio());
//    }
//
//    @Test
//    @DisplayName("Deve calcular taxa de poupança como zero quando não houver renda")
//    void shouldHandleZeroIncomeForSavingsRatio() {
//        UUID userId = UUID.randomUUID();
//        Account acc = new Account(UUID.randomUUID(), userId, AccountType.CHECKING, "Cash", BigDecimal.TEN);
//
//        when(accountRepository.findByUserId(userId)).thenReturn(List.of(acc));
//        when(transactionRepository.sumTransactionsByAccountsAndPeriod(anyList(), any(), any(), any()))
//                .thenReturn(BigDecimal.ZERO);
//
//        KpiDashboardResponse result = useCase.execute(userId, 5, 2026);
//
//        assertEquals(BigDecimal.ZERO, result.savingsRatio());
//    }
//}