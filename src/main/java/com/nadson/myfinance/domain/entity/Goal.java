package com.nadson.myfinance.domain.entity;

import com.nadson.myfinance.domain.exception.BusinessRuleException;

import java.math.BigDecimal;
import java.util.UUID;

public class Goal {
    private UUID id;
    private UUID userId;
    private String description;
    private BigDecimal targetAmount;
    private BigDecimal currentAmount;


    
    public Goal(UUID id, UUID userId, String description, BigDecimal targetAmount, BigDecimal currentAmount) {
        validate(userId,description, targetAmount, currentAmount);
        this.id = (id == null) ? UUID.randomUUID() : id;
        this.userId = userId;
        this.description = description;
        this.targetAmount = targetAmount;
        this.currentAmount = (currentAmount == null)? BigDecimal.ZERO : currentAmount;    }

    public UUID getId() {
        return id;
    }


    public UUID getUserId() {
        return userId;
    }



    public String getDescription() {
        return description;
    }

    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    public BigDecimal getCurrentAmount() {
        return currentAmount;
    }
    public void addAmount(BigDecimal amount){
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0){
            throw new BusinessRuleException("Amount must be greater than zero.");
        }
        this.currentAmount = this.currentAmount.add(amount);
    }
    public void validate(UUID userId,String description, BigDecimal targetAmount, BigDecimal currentAmount) {
      
        if (userId == null) {
            throw new BusinessRuleException("User Id is required.");
        }
        if (description == null|| description.isBlank()) {
            throw new BusinessRuleException("Description is required.");
        }
      
        if (targetAmount == null || targetAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("Target amount must be greater than zero.");
        }
        if (currentAmount == null || currentAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessRuleException("Current amount cannot be negative.");
        }
    }
}
