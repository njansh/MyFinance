package com.nadson.myfinance.infrastructure.adapter.web.dto.response;

import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.domain.enums.TransactionType;

import java.math.BigDecimal;
import java.util.UUID;

public record TransactionResponse(String description,
                                  BigDecimal amount,
                                  TransactionType type,
                                  UUID accountId,
                                  UUID categoryId,
                                  boolean isTransfer) {
    public static TransactionResponse fromDomain(Transaction t){
        return new TransactionResponse(t.getDescription(),
                t.getAmount(),
                t.getType(),
                t.getAccountId(),
                t.getCategoryId(),
                t.isTransfer());
    }
}
