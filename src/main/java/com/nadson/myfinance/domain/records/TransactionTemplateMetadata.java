package com.nadson.myfinance.domain.records;

import com.nadson.myfinance.domain.enums.TransactionStatus;
import com.nadson.myfinance.domain.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionTemplateMetadata(
        UUID transactionId,
        UUID transferID,
        String description,
        BigDecimal amount,
        BigDecimal accountBalanceAfter,
        LocalDateTime date,
        TransactionType type,
        UUID accountId,
        UUID categoryId,
        boolean isTransfer,
        TransactionStatus status,
        UUID templateId
) {
}
