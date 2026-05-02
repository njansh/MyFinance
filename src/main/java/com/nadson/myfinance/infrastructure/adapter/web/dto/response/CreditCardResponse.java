package com.nadson.myfinance.infrastructure.adapter.web.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record  CreditCardResponse(UUID id, String name, BigDecimal creditLimit, BigDecimal availableLimit){
}
