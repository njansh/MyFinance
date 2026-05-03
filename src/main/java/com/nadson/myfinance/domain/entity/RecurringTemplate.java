package com.nadson.myfinance.infrastructure.config;

import com.nadson.myfinance.domain.enums.TransactionType;

import java.math.BigDecimal;
import java.util.UUID;

public  class RecurringTemplate {
    private UUID id;
    private UUID userId;
    private UUID accountId;
    private UUID categoryId;
    private String description;
    private BigDecimal expectedAmount;
    private TransactionType type;
    private int frequencyDay;
    private boolean active;
    private Integer lastExecutedMonth;
    private Integer lastExecutedYear;
    //... Construtor com validações e Getters
}
