package com.nadson.myfinance.domain.entity;

import com.nadson.myfinance.domain.exception.BusinessRuleException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Goal {
    private UUID id;
    private UUID userId;
    private String description;
    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    private List<UUID> accountIds;

    public Goal(UUID id, UUID userId, String description, BigDecimal targetAmount, BigDecimal currentAmount, List<UUID> accountIds) {
        validate(userId, description, targetAmount);
        this.id = (id == null) ? UUID.randomUUID() : id;
        this.userId = userId;
        this.description = description;
        this.targetAmount = targetAmount;
        this.currentAmount = (currentAmount == null) ? BigDecimal.ZERO : currentAmount;
        this.accountIds = (accountIds == null) ? new ArrayList<>() : accountIds;
    }

    public void update(String newDescription, BigDecimal newTargetAmount, List<UUID> newAccountIds) {
        if (newDescription != null) {
            if (newDescription.isBlank()) {
                throw new BusinessRuleException("Description cannot be blank.");
            }
            this.description = newDescription;
        }

        if (newTargetAmount != null) {
            if (newTargetAmount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessRuleException("Target amount must be greater than zero.");
            }
            this.targetAmount = newTargetAmount;
        }

        if (newAccountIds != null) {
            this.accountIds = newAccountIds;
        }
    }

    public void addAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("Amount must be greater than zero.");
        }
        this.currentAmount = this.currentAmount.add(amount);
    }

    public void subtractAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("Amount must be greater than zero.");
        }
        this.currentAmount = this.currentAmount.subtract(amount);

        if (this.currentAmount.compareTo(BigDecimal.ZERO) < 0) {
            this.currentAmount = BigDecimal.ZERO;
        }
    }

    private void validate(UUID userId, String description, BigDecimal targetAmount) {
        if (userId == null) throw new BusinessRuleException("User Id is required.");
        if (description == null || description.isBlank()) throw new BusinessRuleException("Description is required.");
        if (targetAmount == null || targetAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("Target amount must be greater than zero.");
        }
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public String getDescription() { return description; }
    public BigDecimal getTargetAmount() { return targetAmount; }
    public BigDecimal getCurrentAmount() { return currentAmount; }
    public List<UUID> getAccountIds() { return new ArrayList<>(accountIds); }
}