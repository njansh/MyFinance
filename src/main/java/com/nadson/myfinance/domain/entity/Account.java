package com.nadson.myfinance.domain.entity;

import com.nadson.myfinance.domain.enums.AccountType;

import java.math.BigDecimal;
import java.util.UUID;

public class Account {
    private UUID accountID;
    private UUID userId;
    private AccountType type;
    private String name;
    private BigDecimal balance;


    public Account(UUID accountID, UUID userId, AccountType type, String name, BigDecimal balance) {
        this.accountID = accountID;
        this.userId = userId;
        this.type = type;
        this.name = name;
        this.balance = (balance == null) ? BigDecimal.ZERO : balance;
    }
    public Account(UUID userId, String name, AccountType type) {
        this(UUID.randomUUID(), userId, type, name, BigDecimal.ZERO);
    }

    public BigDecimal deposit(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        this.balance = this.balance.add(amount);
        return this.balance;
    }

    public BigDecimal withdraw(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        this.balance = this.balance.subtract(amount);
        return this.balance;
    }

    public UUID getAccountId() {
        return accountID;
    }

    public UUID getUserId() {
        return userId;
    }

    public AccountType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getBalance() {
        return balance;
    }

}
