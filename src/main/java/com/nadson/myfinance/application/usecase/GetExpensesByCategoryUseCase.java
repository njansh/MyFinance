package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.GetExpensesByCategoryPort;
import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
import com.nadson.myfinance.domain.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class GetExpensesByCategoryUseCase implements GetExpensesByCategoryPort {
    private final TransactionRepositoryPort transactionRepository;

    public GetExpensesByCategoryUseCase(TransactionRepositoryPort transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Map<String, BigDecimal> execute(UUID accountId, LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate!= null && endDate!= null) {
            return transactionRepository.getSumByCategoryAndTypeAndDateBetween(accountId, TransactionType.EXPENSE, startDate, endDate);
        }
        return transactionRepository.getSumByCategoryAndType(accountId, TransactionType.EXPENSE);
    }
}