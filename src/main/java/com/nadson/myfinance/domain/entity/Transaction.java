package com.nadson.myfinance.domain.entity;

import com.nadson.myfinance.domain.enums.TransactionType;
import com.nadson.myfinance.domain.exception.BusinessRuleException;
import com.nadson.myfinance.domain.exception.InvalidTransactionValueException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Transaction {
    private UUID transactionId;
    private UUID transferID;
    private String description;
    private BigDecimal amount;
    private LocalDateTime date;
    private TransactionType type;
    private UUID accountId;
    private UUID categoryId;
    private boolean isTransfer;

[    public Transaction(UUID transactionId, String description, BigDecimal amount,
                       LocalDateTime date, TransactionType type, UUID accountId, UUID categoryId, boolean isTransfer, UUID transferID) {

    public Transaction(UUID transactionId, String description, BigDecimal amount, LocalDateTime date, TransactionType type, UUID accountId, UUID categoryId, boolean isTransfer, UUID transferID) {
        validate(description, amount, date, type, accountId);
        this.transactionId = transactionId;
        if(transferID == null) {
            this.transferID = null;
        } else {
            this.transferID = transferID;
        }
        this.transferID = transferID;
        this.description = description;
      this.description = description;
        this.amount = amount;
        this.date = date;
        this.type = type;
        this.accountId = accountId;
        this.categoryId = categoryId;
        this.isTransfer = isTransfer;
    }
    public Transaction(UUID accountId, BigDecimal amount, TransactionType type, String description, UUID transferID) {
        LocalDateTime now = LocalDateTime.now();
        validate(description, amount,now, type, accountId);
        this.transactionId = UUID.randomUUID();
        this.date = now;
        this.accountId = accountId;
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.isTransfer = true;
        this.categoryId = null;
        this.transferID = transferID;
    }
    public void updateCategory(UUID categoryId) {
        if (categoryId == null) {
            throw new BusinessRuleException("Category ID cannot be null");
        }
        this.categoryId = categoryId;
    }
    public void updateDetails(String description, BigDecimal amount, LocalDateTime date, TransactionType type, UUID accountId, UUID categoryId) {
        if (description != null) {
            if (description.trim().isEmpty()) {
                throw new BusinessRuleException("Description cannot be empty");
            }
            this.description = description;
        }

        if (amount != null && amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionValueException("Transaction amount must be greater than zero");
        }
        this.amount = amount;


        if (date != null) {
            this.date = date;
        }

        if (type != null) {
            this.type = type;
        }

        if (accountId != null) {
            this.accountId = accountId;
        }

        if (categoryId != null) {
            this.categoryId = categoryId;
        }
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
    public UUID getTransferID() {
        return transferID;
    }


    private void validate(String description, BigDecimal amount, LocalDateTime date, TransactionType type, UUID accountId) {
        if (description == null || description.trim().isEmpty()) {
            throw new BusinessRuleException("Description cannot be null or empty");
        }
        if (amount == null) {
            throw new BusinessRuleException("Amount cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("Amount must be greater than zero");
        }
        if (date == null) {
            throw new BusinessRuleException("Date cannot be null");
        }
        if (type == null) {
            throw new BusinessRuleException("Transaction type cannot be null");
        }
        if (accountId == null) {
            throw new BusinessRuleException("Account cannot be null");
        }