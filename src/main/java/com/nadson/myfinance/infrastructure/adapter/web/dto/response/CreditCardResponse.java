package com.nadson.myfinance.infrastructure.adapter.web.dto.response;

import com.nadson.myfinance.domain.entity.BillingCycle;
import com.nadson.myfinance.domain.records.CreditCardWithBalanceDTO;

import java.math.BigDecimal;
import java.util.UUID;

public record CreditCardResponse(UUID id, UUID accountId, UUID userId, String name, BigDecimal creditLimit, BigDecimal availableLimit) {

    public static CreditCardResponse from(CreditCardWithBalanceDTO dto) {
        return new CreditCardResponse(
                dto.id(),
                dto.accountId(),
                dto.userId(),
                dto.name(),
                dto.creditLimit(),
                dto.availableLimit()
        );
    }

}
