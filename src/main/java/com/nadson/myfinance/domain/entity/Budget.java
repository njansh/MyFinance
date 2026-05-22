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

    private boolean alertedEightyPercent;
    private boolean alertedOneHundredPercent;

    public Budget(UUID id, UUID userId, UUID categoryId, int month, int year,
                  BigDecimal limitAmount, BigDecimal spentAmount,
                  boolean alertedEightyPercent, boolean alertedOneHundredPercent) {
        validate(userId, categoryId, month, year, limitAmount);
        this.id = id == null ? UUID.randomUUID() : id;
        this.userId = userId;
        this.categoryId = categoryId;
        this.month = month;
        this.year = year;
        this.limitAmount = limitAmount;
        this.spentAmount = spentAmount == null ? BigDecimal.ZERO : spentAmount;
        this.alertedEightyPercent = alertedEightyPercent;
        this.alertedOneHundredPercent = alertedOneHundredPercent;
    }

    public Budget(UUID id, UUID userId, UUID categoryId, int month, int year, BigDecimal limitAmount) {
        this(id, userId, categoryId, month, year, limitAmount, BigDecimal.ZERO, false, false);
    }

    public void updateLimit(BigDecimal newLimit) {
        validate(this.userId, this.categoryId, this.month, this.year, newLimit);
        this.limitAmount = newLimit;
        this.alertedEightyPercent = false;
        this.alertedOneHundredPercent = false;

        BigDecimal usage = getUsagePercentage();
        if (usage.compareTo(BigDecimal.ONE) >= 0) {
            this.alertedOneHundredPercent = true;
            this.alertedEightyPercent = true;
        } else if (usage.compareTo(new BigDecimal("0.80")) >= 0) {
            this.alertedEightyPercent = true;
        }
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
        if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0) {
            this.spentAmount = this.spentAmount.add(amount);
        }
    }

    public void removeExpense(BigDecimal amount) {
        if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0) {
            this.spentAmount = this.spentAmount.subtract(amount);

            if (this.spentAmount.compareTo(BigDecimal.ZERO) < 0) {
                this.spentAmount = BigDecimal.ZERO;
            }


            BigDecimal usage = getUsagePercentage();
            BigDecimal eightyPercent = new BigDecimal("0.80");

            if (usage.compareTo(BigDecimal.ONE) < 0) {
                this.alertedOneHundredPercent = false;
            }
            if (usage.compareTo(eightyPercent) < 0) {
                this.alertedEightyPercent = false;
            }
        }
    }

    public BigDecimal getUsagePercentage() {
        if (limitAmount.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return spentAmount.divide(limitAmount, 4, RoundingMode.HALF_UP);
    }

    public boolean shouldAlertEightyPercent() {
        if (alertedEightyPercent) return false;

        BigDecimal eightyPercent = new BigDecimal("0.80");
        if (getUsagePercentage().compareTo(eightyPercent) >= 0 && getUsagePercentage().compareTo(BigDecimal.ONE) < 0) {
            this.alertedEightyPercent = true;
            return true;
        }
        return false;
    }

    public boolean shouldAlertOneHundredPercent() {
        if (alertedOneHundredPercent) return false;

        if (getUsagePercentage().compareTo(BigDecimal.ONE) >= 0) {
            this.alertedOneHundredPercent = true;
            this.alertedEightyPercent = true;
            return true;
        }
        return false;
    }

    public boolean isExceeded() {
        return spentAmount.compareTo(limitAmount) > 0;
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public UUID getCategoryId() { return categoryId; }
    public int getMonth() { return month; }
    public int getYear() { return year; }
    public BigDecimal getLimitAmount() { return limitAmount; }
    public BigDecimal getSpentAmount() { return spentAmount; }
    public boolean isAlertedEightyPercent() { return alertedEightyPercent; }
    public boolean isAlertedOneHundredPercent() { return alertedOneHundredPercent; }
}