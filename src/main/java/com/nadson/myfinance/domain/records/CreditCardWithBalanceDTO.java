package com.nadson.myfinance.domain.records;

import java.math.BigDecimal;
import java.util.UUID;

public record CreditCardWithBalanceDTO(
        UUID id,
        UUID accountId,
        UUID userId,
        String name,
        BigDecimal creditLimit,
        BigDecimal availableLimit,
        int closingDay,
        int dueDay
) {}
