package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.GetFinancialDashboardKpisPort;
import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.enums.AccountType;
import com.nadson.myfinance.domain.enums.TransactionType;
import com.nadson.myfinance.infrastructure.adapter.web.dto.response.KpiDashboardResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

public class GetFinancialDashboardKpisUseCase implements GetFinancialDashboardKpisPort {

    private final AccountRepositoryPort accountRepository;
    private final TransactionRepositoryPort transactionRepository;

    public GetFinancialDashboardKpisUseCase(AccountRepositoryPort accountRepository, TransactionRepositoryPort transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public KpiDashboardResponse execute(UUID userId, Integer month, Integer year) {
        List<Account> accounts = accountRepository.findByUserId(userId);
        if (accounts.isEmpty()) {
            return new KpiDashboardResponse(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }

        // Patrimônio Líquido (Soma simples de saldos atuais - essa lista é sempre pequena)
        BigDecimal netWorth = accounts.stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Definição do período
        YearMonth targetMonth = (month!= null && year!= null)? YearMonth.of(year, month) : YearMonth.now();
        LocalDateTime startDate = targetMonth.atDay(1).atStartOfDay();
        LocalDateTime endDate = targetMonth.atEndOfMonth().atTime(23, 59, 59);

        List<UUID> allAccountIds = accounts.stream().map(Account::getAccountId).toList();

        // CÁLCULOS DELEGADOS AO BANCO (Performance Máxima)
        BigDecimal monthlyIncome = transactionRepository.sumTransactionsByAccountsAndPeriod(allAccountIds, startDate, endDate, TransactionType.INCOME);
        BigDecimal monthlyExpense = transactionRepository.sumTransactionsByAccountsAndPeriod(allAccountIds, startDate, endDate, TransactionType.EXPENSE);

        monthlyIncome = (monthlyIncome == null)? BigDecimal.ZERO : monthlyIncome;
        monthlyExpense = (monthlyExpense == null)? BigDecimal.ZERO : monthlyExpense;

        BigDecimal cashFlow = monthlyIncome.subtract(monthlyExpense);

        // Taxa de Poupança (Apenas transferências para contas tipo INVESTMENT)
        List<UUID> investmentAccountIds = accounts.stream()
                .filter(acc -> acc.getType() == AccountType.INVESTMENT)
                .map(Account::getAccountId)
                .toList();

        BigDecimal savingsAmount = BigDecimal.ZERO;
        if (!investmentAccountIds.isEmpty()) {
            savingsAmount = transactionRepository.sumSavingsByAccountsAndPeriod(investmentAccountIds, startDate, endDate);
            savingsAmount = (savingsAmount == null)? BigDecimal.ZERO : savingsAmount;
        }

        BigDecimal savingsRatio = BigDecimal.ZERO;
        if (monthlyIncome.compareTo(BigDecimal.ZERO) > 0) {
            savingsRatio = savingsAmount.divide(monthlyIncome, 4, RoundingMode.HALF_EVEN)
                    .multiply(new BigDecimal("100"));
        }

        return new KpiDashboardResponse(netWorth, monthlyIncome, monthlyExpense, cashFlow, savingsRatio);
    }
}