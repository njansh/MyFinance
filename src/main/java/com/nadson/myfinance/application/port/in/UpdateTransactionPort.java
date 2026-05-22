package com.nadson.myfinance.application.port.in;

import com.nadson.myfinance.application.usecase.CreateTransactionUseCase.TransactionResult;
import com.nadson.myfinance.domain.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public interface UpdateTransactionPort {
    TransactionResult execute(UUID transactionId, String description, BigDecimal amount,
                              LocalDateTime date, TransactionType type, UUID accountId, UUID categoryId);
}