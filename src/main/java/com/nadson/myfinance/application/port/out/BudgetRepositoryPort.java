package com.nadson.myfinance.application.port.out;

import com.nadson.myfinance.domain.entity.Budget;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BudgetRepositoryPort {

    Budget save(Budget budget);

    Optional<Budget> findById(UUID id);

    Budget findByUserIdAndCategoryIdAndMonthAndYear(UUID userId, UUID categoryId, int month, int year);

    List<Budget> findByUserIdAndMonthAndYear(UUID userId, int month, int year);

    List<Budget> findByUserId(UUID userId);


    void deleteById(UUID id);

    void deleteAllByUserId(UUID userId);
}