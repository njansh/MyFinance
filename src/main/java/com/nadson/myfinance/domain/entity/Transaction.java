package com.nadson.myfinance.domain.entity;

import com.nadson.myfinance.domain.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Transaction {
    private UUID transactionId;
    private String description;
    private BigDecimal amount;
    private LocalDateTime date;
    private TransactionType type;
    private UUID accountId;
    private UUID categoryId;
    private boolean isTransfer;

    public Transaction(UUID transactionId, String description, BigDecimal amount, LocalDateTime date, TransactionType type, UUID accountId, UUID categoryId, boolean isTransfer) {
        validate(description, amount, date, type, accountId);
        this.transactionId = transactionId;
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.type = type;
        this.accountId = accountId;
        this.categoryId = categoryId;
        this.isTransfer = isTransfer;
    }
    public Transaction(UUID accountId, BigDecimal amount, TransactionType type, String description) {
        this.transactionId = UUID.randomUUID();
        this.date = LocalDateTime.now();
        this.accountId = accountId;
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.isTransfer = true;
        this.categoryId = null;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public TransactionType getType() {
        return type;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public boolean isTransfer() {
        return isTransfer;
    }

    private void validate(String description, BigDecimal amount, LocalDateTime date, TransactionType type, UUID accountId) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be null or empty");
        }
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("Transaction type cannot be null");
        }
        if (accountId == null) {
            throw new IllegalArgumentException("Account cannot be null");
        }
    }
}
