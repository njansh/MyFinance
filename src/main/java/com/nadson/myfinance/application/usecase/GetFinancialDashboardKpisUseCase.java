package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.GetFinancialDashboardKpisPort;
import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.entity.Transaction;
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

        BigDecimal netWorth = accounts.stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        LocalDateTime startDate;
        LocalDateTime endDate;
        if (month!= null && year!= null) {
            YearMonth yearMonth = YearMonth.of(year, month);
            startDate = yearMonth.atDay(1).atStartOfDay();
            endDate = yearMonth.atEndOfMonth().atTime(23, 59, 59);
        } else {
            YearMonth currentMonth = YearMonth.now();
            startDate = currentMonth.atDay(1).atStartOfDay();
            endDate = currentMonth.atEndOfMonth().atTime(23, 59, 59);
        }

        List<Transaction> monthlyTransactions = accounts.stream()
                .flatMap(acc -> transactionRepository.findAllByAccountIdAndDateBetween(acc.getAccountId(), startDate, endDate).stream())
                .toList();

        BigDecimal monthlyIncome = monthlyTransactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME &&!t.isTransfer())
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal monthlyExpense = monthlyTransactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE &&!t.isTransfer())
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal cashFlow = monthlyIncome.subtract(monthlyExpense);

        List<UUID> investmentAccountIds = accounts.stream()
                .filter(acc -> acc.getType() == AccountType.INVESTMENT)
                .map(Account::getAccountId)
                .toList();

        BigDecimal savingsAmount = monthlyTransactions.stream()
                .filter(t -> t.isTransfer() && t.getType() == TransactionType.INCOME && investmentAccountIds.contains(t.getAccountId()))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal savingsRatio = BigDecimal.ZERO;
        if (monthlyIncome.compareTo(BigDecimal.ZERO) > 0) {
            savingsRatio = savingsAmount.divide(monthlyIncome, 4, RoundingMode.HALF_EVEN)
                    .multiply(new BigDecimal("100"));
        }

        return new KpiDashboardResponse(netWorth, monthlyIncome, monthlyExpense, cashFlow, savingsRatio);
    }
}