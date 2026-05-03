package com.nadson.myfinance.application.port.out;

import com.nadson.myfinance.domain.entity.Budget;

import java.util.UUID;

public interface BudgetRepositoryPort {
    Budget save(Budget budget);
    Budget findByUserIdAndCategoryIdAndMonthAndYear(UUID userId, UUID categoryId, int month, int year);

    void deleteAllByUserId(UUID userId);
}
