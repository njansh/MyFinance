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

    public Account() {
    }

    public Account(UUID accountID, UUID userId, AccountType type, String name, BigDecimal balance) {
        this.accountID = accountID;
        this.userId = userId;
        this.type = type;
        this.name = name;
        this.balance = balance;
    }


}
