package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.GetExpensesByCategoryPort;
import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
import com.nadson.myfinance.application.port.out.CategoryRepositoryPort;
import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.domain.enums.TransactionType;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GetExpensesByCategoryUseCase implements GetExpensesByCategoryPort {
    private final TransactionRepositoryPort transactionRepository;
    private final CategoryRepositoryPort categoryRepository;

    public GetExpensesByCategoryUseCase(TransactionRepositoryPort transactionRepository, CategoryRepositoryPort categoryRepository) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;

    }

       @Override
    public Map<String, BigDecimal> execute(UUID accountId) {
        List<Transaction>transactions=transactionRepository.findByAccountId(accountId);
        Map<String, BigDecimal> report=new HashMap<>();
        transactions.stream().filter(t -> t.getType() == TransactionType.EXPENSE).forEach(t -> {String categoryName=categoryRepository.findById(t.getCategoryId()).getName();

        report.put(categoryName, report.getOrDefault(categoryName, BigDecimal.ZERO).add(t.getAmount()));});
        return report;

    }
}
