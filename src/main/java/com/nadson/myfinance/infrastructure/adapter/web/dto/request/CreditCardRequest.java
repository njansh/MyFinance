package com.nadson.myfinance.infrastructure.adapter.web.dto;

import java.math.BigDecimal;
import java.util.UUID;

record CreditCardRequest(String name, BigDecimal creditLimit, int closingDay, int dueDay, UUID accountId){

}
