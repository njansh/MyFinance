package com.nadson.myfinance.infrastructure.adapter.web.dto.response;

import com.nadson.myfinance.domain.entity.Account;
import java.math.BigDecimal;
import java.util.UUID;

public record AccountResponse(
    UUID accountId,
    UUID userId,
    String type,
    String name,
    BigDecimal balance
) {
    public static AccountResponse fromDomain(Account account) {
        return new AccountResponse(
            account.getAccountId(),
            account.getUserId(),
            account.getType().name(),
            account.getName(),
            account.getBalance()
        );
    }
}
