package com.nadson.myfinance.infrastructure.adapter.web.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreditCardTransactionRequest(BigDecimal amount, LocalDate date, int installments) {
}
