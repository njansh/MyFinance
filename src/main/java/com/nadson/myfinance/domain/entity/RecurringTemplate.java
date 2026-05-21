package com.nadson.myfinance.domain.entity;

import com.nadson.myfinance.domain.enums.TransactionType;
import com.nadson.myfinance.domain.exception.BusinessRuleException;

import java.math.BigDecimal;
import java.util.UUID;

public class RecurringTemplate {
    private UUID id;
    private UUID userId;
    private UUID accountId;
    private UUID categoryId;
    private String description;
    private BigDecimal expectedAmount;
    private TransactionType type;
    private int frequencyDay;
    private boolean active;
    private Integer lastExecutedMonth;
    private Integer lastExecutedYear;

    public RecurringTemplate(UUID id, UUID userId, UUID accountId, UUID categoryId, String description,
                             BigDecimal expectedAmount, TransactionType type, int frequencyDay, boolean active) {
        validate(userId, accountId, description, expectedAmount, type, frequencyDay);
        this.id = id;
        this.userId = userId;
        this.accountId = accountId;
        this.categoryId = categoryId;
        this.description = description;
        this.expectedAmount = expectedAmount;
        this.type = type;
        this.frequencyDay = frequencyDay;
        this.active = active;
    }

    private void validate(UUID userId, UUID accountId, String description, BigDecimal expectedAmount,
                          TransactionType type, int frequencyDay) {
        if (userId == null) throw new BusinessRuleException("User ID is required");
        if (accountId == null) throw new BusinessRuleException("Account ID is required");
        if (description == null || description.isBlank()) throw new BusinessRuleException("Description is required");
        if (expectedAmount == null || expectedAmount.compareTo(BigDecimal.ZERO) <= 0)
            throw new BusinessRuleException("Expected amount must be greater than zero");
        if (type == null) throw new BusinessRuleException("Transaction type is required");
        if (frequencyDay < 1 || frequencyDay > 31) throw new BusinessRuleException("Invalid frequency day");
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public UUID getAccountId() { return accountId; }
    public UUID getCategoryId() { return categoryId; }
    public String getDescription() { return description; }
    public BigDecimal getExpectedAmount() { return expectedAmount; }
    public BigDecimal getAmount() { return expectedAmount; }
    public TransactionType getType() { return type; }
    public int getFrequencyDay() { return frequencyDay; }
    public boolean isActive() { return active; }
    public Integer getLastExecutedMonth() { return lastExecutedMonth; }
    public Integer getLastExecutedYear() { return lastExecutedYear; }

    public void setLastExecution(int month, int year) {
        this.lastExecutedMonth = month;
        this.lastExecutedYear = year;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
