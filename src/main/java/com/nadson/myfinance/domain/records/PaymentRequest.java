package com.nadson.myfinance.domain.records;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentRequest(UUID accountId, BigDecimal amount) {}
