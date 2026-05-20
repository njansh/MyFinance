package com.nadson.myfinance.domain.entity;

import com.nadson.myfinance.domain.exception.BusinessRuleException;

import java.math.BigDecimal;
import java.util.UUID;

public class CreditCard {
    private UUID id;
    private UUID accountId;
    private UUID userId;
    private String name;
    private BigDecimal creditLimit;
    private int closingDay;
    private int dueDay;

    public CreditCard(UUID id, UUID accountId, UUID userId, String name, BigDecimal creditLimit, int closingDay, int dueDay) {
        validate(name, creditLimit, closingDay, dueDay);
        this.id = id;
        this.accountId = accountId;
        this.userId = userId;
        this.name = name;
        this.creditLimit = creditLimit;
        this.closingDay = closingDay;
        this.dueDay = dueDay;
    }




    private void validate(String name, BigDecimal creditLimit, int closingDay, int dueDay) {
        if (name == null || name.isBlank()) throw new BusinessRuleException("Name is required");

        if (creditLimit == null || creditLimit.compareTo(BigDecimal.ZERO) <= 0)
            throw new BusinessRuleException("Credit limit must be greater than zero");

        if (closingDay < 1 || closingDay > 31) throw new BusinessRuleException("Invalid closing day");

        if (dueDay < 1 || dueDay > 31) throw new BusinessRuleException("Invalid due day");
    }

    public UUID getId() { return id; }
    public UUID getAccountId() { return accountId; }
    public UUID getUserId() { return userId; }
    public String getName() { return name; }
    public BigDecimal getCreditLimit() { return creditLimit; }
    public int getClosingDay() { return closingDay; }
    public int getDueDay() { return dueDay; }
}

