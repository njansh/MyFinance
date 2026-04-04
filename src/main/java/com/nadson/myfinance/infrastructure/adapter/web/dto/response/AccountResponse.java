package com.nadson.myfinance.infrastructure.adapter.web.dto.response;

import com.nadson.myfinance.domain.entity.Account;
import java.math.BigDecimal;
import java.util.UUID;

public class AccountResponse {
    private UUID accountId;
    private UUID userId;
    private String type;
    private String name;
    private BigDecimal balance;

    public AccountResponse() {
    }

    public AccountResponse(UUID accountId, UUID userId, String type, String name, BigDecimal balance) {
        this.accountId = accountId;
        this.userId = userId;
        this.type = type;
        this.name = name;
        this.balance = balance;
    }

    public static AccountResponse fromDomain(Account account) {
        return new AccountResponse(
            account.getAccountId(),
            account.getUserId(),
            account.getType().name(),
            account.getName(),
            account.getBalance()
        );
    }

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

}
