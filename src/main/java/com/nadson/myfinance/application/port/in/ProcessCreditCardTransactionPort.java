package com.nadson.myfinance.application.port.in;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public interface ProcessCreditCardTransactionPort {
    void execute(UUID userId, UUID creditCardId, UUID categoryId, String description, BigDecimal amount, LocalDate transactionDate, int installments);}