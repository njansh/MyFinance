package com.nadson.myfinance.infrastructure.adapter.web.dto.response;

import com.nadson.myfinance.domain.entity.CreditCard;
import com.nadson.myfinance.domain.records.CreditCardWithBalanceDTO;

import java.math.BigDecimal;
import java.util.UUID;

public record CreditCardResponse(
        UUID id,
        String name,
        BigDecimal creditLimit,
        BigDecimal availableLimit,
        int closingDay, // AQUI
        int dueDay      // AQUI
) {
    public static CreditCardResponse from(CreditCardWithBalanceDTO dto) {
        return new CreditCardResponse(
                dto.id(),
                dto.name(),
                dto.creditLimit(),
                dto.availableLimit(),
                // Se o seu DTO ainda não tiver esses campos, vai precisar colocar nela também!
                dto.closingDay(),
                dto.dueDay()
        );
    }

    // Se você mapeia direto da entidade em algum lugar:
    public static CreditCardResponse from(CreditCard card) {
        return new CreditCardResponse(
                card.getId(),
                card.getName(),
                card.getCreditLimit(),
                card.getCreditLimit(), // ou lógica de limite
                card.getClosingDay(),
                card.getDueDay()
        );
    }
}