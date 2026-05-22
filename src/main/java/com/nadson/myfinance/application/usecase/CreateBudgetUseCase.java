package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.CreateBudgetPort;
import com.nadson.myfinance.application.port.out.BudgetRepositoryPort;
import com.nadson.myfinance.application.port.out.CategoryRepositoryPort;
import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
import com.nadson.myfinance.application.port.out.UserRepositoryPort;
import com.nadson.myfinance.domain.entity.Budget;
import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.domain.enums.TransactionType;
import com.nadson.myfinance.domain.exception.BusinessRuleException;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class CreateBudgetUseCase implements CreateBudgetPort {
    private final BudgetRepositoryPort repository;
    private final UserRepositoryPort userRepository;
    private final CategoryRepositoryPort categoryRepository;
    private final TransactionRepositoryPort transactionRepository;

    public CreateBudgetUseCase(BudgetRepositoryPort repository,
                               UserRepositoryPort userRepository,
                               CategoryRepositoryPort categoryRepository,
                               TransactionRepositoryPort transactionRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Budget execute(UUID userId, UUID categoryId, int month, int year, BigDecimal limitAmount) {
        if (userRepository.findById(userId) == null) {
            throw new BusinessRuleException("User not found");
        }
        if (categoryRepository.findById(categoryId) == null) {
            throw new BusinessRuleException("Category not found");
        }

        List<Transaction> transactions = transactionRepository.findAllByUserIdAndCategoryIdAndMonthAndYear(userId, categoryId, month, year);

        BigDecimal spentAmount = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        System.out.println("DEBUG: Gastos encontrados para categoria " + categoryId + ": " + spentAmount);
        Budget budget = new Budget(
                UUID.randomUUID(),
                userId,
                categoryId,
                month,
                year,
                limitAmount,
                (spentAmount != null ? spentAmount : BigDecimal.ZERO), // Tratamento de segurança,
                false,
                false
        );
        return repository.save(budget);
    }
}