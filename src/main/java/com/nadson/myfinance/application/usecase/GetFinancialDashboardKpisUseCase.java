package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.GetFinancialDashboardKpisPort;
import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
import com.nadson.myfinance.application.port.out.RecurringTemplateRepositoryPort;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.entity.RecurringTemplate;
import com.nadson.myfinance.domain.enums.TransactionType;
import com.nadson.myfinance.infrastructure.adapter.web.dto.response.KpiDashboardResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

public class GetFinancialDashboardKpisUseCase implements GetFinancialDashboardKpisPort {

    private final AccountRepositoryPort accountRepository;
    private final TransactionRepositoryPort transactionRepository;
    private final RecurringTemplateRepositoryPort recurringTemplateRepository;

    public GetFinancialDashboardKpisUseCase(
            AccountRepositoryPort accountRepository,
            TransactionRepositoryPort transactionRepository,
            RecurringTemplateRepositoryPort recurringTemplateRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.recurringTemplateRepository = recurringTemplateRepository;
    }

    @Override
    public KpiDashboardResponse execute(UUID userId, Integer month, Integer year) {
        List<Account> accounts = accountRepository.findByUserId(userId);
        if (accounts.isEmpty()) {
            return new KpiDashboardResponse(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }

        YearMonth targetMonth = (month != null && year != null) ? YearMonth.of(year, month) : YearMonth.now();
        LocalDateTime startDate = targetMonth.atDay(1).atStartOfDay();
        LocalDateTime endDate = targetMonth.atEndOfMonth().atTime(23, 59, 59);

        List<UUID> allAccountIds = accounts.stream().map(Account::getAccountId).toList();

        BigDecimal historicalIncome = transactionRepository.sumBalanceBeforeDate(allAccountIds, startDate, TransactionType.INCOME);
        BigDecimal historicalExpense = transactionRepository.sumBalanceBeforeDate(allAccountIds, startDate, TransactionType.EXPENSE);

        historicalIncome = (historicalIncome == null) ? BigDecimal.ZERO : historicalIncome;
        historicalExpense = (historicalExpense == null) ? BigDecimal.ZERO : historicalExpense;

        BigDecimal lastMonthBalance = historicalIncome.subtract(historicalExpense);

        BigDecimal monthlyIncome = transactionRepository.sumTransactionsByAccountsAndPeriod(allAccountIds, startDate, endDate, TransactionType.INCOME);
        BigDecimal monthlyExpense = transactionRepository.sumTransactionsByAccountsAndPeriod(allAccountIds, startDate, endDate, TransactionType.EXPENSE);

        monthlyIncome = (monthlyIncome == null) ? BigDecimal.ZERO : monthlyIncome;
        monthlyExpense = (monthlyExpense == null) ? BigDecimal.ZERO : monthlyExpense;

        BigDecimal currentPeriodNetWorth = accounts.stream()
                .map(acc -> acc.getBalance() != null ? acc.getBalance() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal nextMonthForecast = currentPeriodNetWorth;

        List<RecurringTemplate> activeRecurrences = recurringTemplateRepository.findActiveByUserId(userId);
        if (activeRecurrences != null && !activeRecurrences.isEmpty()) {
            for (RecurringTemplate template : activeRecurrences) {
                if (template.getType() == TransactionType.INCOME) {
                    nextMonthForecast = nextMonthForecast.add(template.getExpectedAmount());
                } else if (template.getType() == TransactionType.EXPENSE) {
                    nextMonthForecast = nextMonthForecast.subtract(template.getExpectedAmount());
                }
            }
        }

        return new KpiDashboardResponse(currentPeriodNetWorth, monthlyIncome, monthlyExpense, lastMonthBalance, nextMonthForecast);
    }
}