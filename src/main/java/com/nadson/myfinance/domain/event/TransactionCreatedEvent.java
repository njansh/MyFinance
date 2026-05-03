package com.nadson.myfinance.domain.event;

import java.math.BigDecimal;
import java.util.UUID;

public record TransactionCreatedEvent(
        UUID userId,
        UUID categoryId,
        BigDecimal amount,
        int month,
        int year
) {}