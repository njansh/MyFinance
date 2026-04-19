package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.GetAccountBalancePort;
import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.domain.enums.TransactionType;
import com.nadson.myfinance.infrastructure.adapter.web.dto.response.BalanceResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class GetAccountBalanceUseCase  implements GetAccountBalancePort {
    private final TransactionRepositoryPort transactionRepositoryPort;

    public GetAccountBalanceUseCase(TransactionRepositoryPort transactionRepositoryPort) {
        this.transactionRepositoryPort = transactionRepositoryPort;
    }


    @Override
    public BalanceResponse execute(UUID accountId, LocalDateTime startDate, LocalDateTime endDate) {
     List<Transaction> transactions;
        if (startDate != null && endDate != null) {
            transactions = transactionRepositoryPort.findAllByAccountIdAndDateBetween(accountId, startDate, endDate);
        } else {
            transactions = transactionRepositoryPort.findAllByAccountId(accountId);
        }
        BigDecimal totalIncomes = transactions.stream().filter(t->t.getType()== TransactionType.INCOME)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO,BigDecimal::add);
        BigDecimal totalExpenses=transactions.stream().filter(t->t.getType()== TransactionType.EXPENSE)
                .map(Transaction::getAmount).reduce(BigDecimal.ZERO,BigDecimal::add);
        BigDecimal balance=totalIncomes.subtract(totalExpenses);
        return new BalanceResponse(totalIncomes,totalExpenses,balance);
    }
}
