package com.nadson.myfinance.infrastructure.adapter.web.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

public record CreditCardRequest(String name, BigDecimal creditLimit, int closingDay, int dueDay, UUID accountId){

}
