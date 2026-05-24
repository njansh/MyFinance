package com.nadson.myfinance.domain.entity;

import com.nadson.myfinance.domain.enums.AccountType;
import com.nadson.myfinance.domain.exception.BusinessRuleException;

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
        validateAmount(amount);
        this.balance = this.balance.add(amount);
        return this.balance;
    }

    public BigDecimal withdraw(BigDecimal amount) {
       validateAmount(amount);
        this.balance = this.balance.subtract(amount);
        return this.balance;
    }
    private void validateAmount(BigDecimal amount) {
        if (amount == null) throw new BusinessRuleException("Amount cannot be null");
        if (amount.compareTo(BigDecimal.ZERO) <= 0) throw new BusinessRuleException("Amount must be greater than zero");
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

    public void update(String name, BigDecimal balance, AccountType type) {
        if (name != null) {
            if (name.isBlank()) throw new BusinessRuleException("Name cannot be blank") ;
            this.name = name;
        } else this.name = this.name;
        if (balance != null) {
            if (balance.compareTo(BigDecimal.ZERO) < 0) throw new BusinessRuleException("Balance cannot be negative");
            this.balance = balance;
        }else this.balance = this.balance;
        if (type != null) {
            this.type = type;
        }else this.type = this.type;
    }

}
