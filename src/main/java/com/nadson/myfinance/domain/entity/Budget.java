package com.nadson.myfinance.domain.entity;

import com.nadson.myfinance.domain.exception.BusinessRuleException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

public class Budget {
    private UUID id;
    private UUID userId;
    private UUID categoryId;
    private int month;
    private int year;
    private BigDecimal limitAmount;
    private BigDecimal spentAmount;

    public Budget(UUID id, UUID userId, UUID categoryId, int month, int year, BigDecimal limitAmount) {
        validate(userId, categoryId, month, year, limitAmount);
        this.id = id == null? UUID.randomUUID() : id;
        this.userId = userId;
        this.categoryId = categoryId;
        this.month = month;
        this.year = year;
        this.limitAmount = limitAmount;
        this.spentAmount = BigDecimal.ZERO;
    }

    private void validate(UUID userId, UUID categoryId, int month, int year, BigDecimal limitAmount) {
        if (userId == null) throw new BusinessRuleException("User ID is required");
        if (categoryId == null) throw new BusinessRuleException("Category ID is required");
        if (month < 1 || month > 12) throw new BusinessRuleException("Invalid month");
        if (year < 2000) throw new BusinessRuleException("Invalid year");
        if (limitAmount == null || limitAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("Limit amount must be greater than zero");
        }
    }

    public void addExpense(BigDecimal amount) {
        if (amount!= null && amount.compareTo(BigDecimal.ZERO) > 0) {
            this.spentAmount = this.spentAmount.add(amount);
        }
    }

    // Calcula se o alerta de 80% ou 100% deve ser disparado
    public boolean isNearingLimit() {
        if (limitAmount.compareTo(BigDecimal.ZERO) == 0) return false;
        BigDecimal percentage = spentAmount.divide(limitAmount, 2, RoundingMode.HALF_UP);
        return percentage.compareTo(new BigDecimal("0.80")) >= 0;
    }

    public boolean isExceeded() {
        return spentAmount.compareTo(limitAmount) > 0;
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public UUID getCategoryId() { return categoryId; }
    public int getMonth() { return month; }
    public int getYear() { return year; }
    public BigDecimal getLimitAmount() { return limitAmount; }
    public BigDecimal getSpentAmount() { return spentAmount; }
}