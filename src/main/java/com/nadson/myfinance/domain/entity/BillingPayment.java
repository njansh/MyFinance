package com.nadson.myfinance.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record BillingPayment(
    UUID id,
    UUID billingCycleId,
    UUID accountId,
    BigDecimal amount,
    LocalDateTime paymentDate
) {}