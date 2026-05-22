package com.nadson.myfinance.infrastructure.adapter.web.dto.response;

import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.domain.enums.TransactionStatus;
import com.nadson.myfinance.domain.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
public record TransactionResponse(
        UUID id, String description, BigDecimal amount, TransactionType type,
        UUID accountId, UUID categoryId, LocalDateTime date, boolean isTransfer,
        UUID transferID, TransactionStatus status, UUID templateId, String budgetAlert
) {
    public static TransactionResponse fromDomain(Transaction t, String alert) {
        return new TransactionResponse(t.getTransactionId(), t.getDescription(), t.getAmount(),
                t.getType(), t.getAccountId(), t.getCategoryId(), t.getDate(), t.isTransfer(),
                t.getTransferID(), t.getStatus(), t.getTemplateId(), alert);
    }
}