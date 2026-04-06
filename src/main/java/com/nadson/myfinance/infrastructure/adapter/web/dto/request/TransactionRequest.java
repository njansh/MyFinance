package com.nadson.myfinance.infrastructure.adapter.web.dto.request;

import com.nadson.myfinance.domain.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


public record TransactionRequest (
         String description,
         BigDecimal amount,
         TransactionType type,
         UUID accountId,
         UUID categoryId,
         boolean isTransfer){
}
