package com.nadson.myfinance.infrastructure.adapter.web.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreditCardTransactionRequest(
        UUID categoryId,
        String description,
        BigDecimal amount,
        LocalDate date,
        int installments
) {}
