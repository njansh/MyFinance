package com.nadson.myfinance.infrastructure.adapter.web.dto.response;

import com.nadson.myfinance.domain.entity.CreditCard;
import com.nadson.myfinance.domain.records.CreditCardWithBalanceDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CreditCardResponseTest {

    @Test
    @DisplayName("Should map from pure Domain Entity to Response DTO")
    void shouldMapFromDomainEntity() {
        UUID id = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        CreditCard card = new CreditCard(id, accountId, userId, "Visa Platinum", new BigDecimal("5000.00"), 5, 15);

        // Testa o primeiro método 'from'
        CreditCardResponse response = CreditCardResponse.from(card);

        assertEquals(id, response.id());
        assertEquals("Visa Platinum", response.name());
        assertEquals(new BigDecimal("5000.00"), response.creditLimit());
        assertEquals(5, response.closingDay());
        assertEquals(15, response.dueDay());
    }

    @Test
    @DisplayName("Should map from CreditCardWithBalanceDTO to Response DTO")
    void shouldMapFromCreditCardWithBalanceDTO() {
        UUID id = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        BigDecimal availableLimit = new BigDecimal("4500.00");

        // Criando o Mock ou Instância do DTO de projeção do pacote com.nadson.myfinance.domain.records
        CreditCardWithBalanceDTO dto = mock(CreditCardWithBalanceDTO.class);
        when(dto.id()).thenReturn(id);
        when(dto.accountId()).thenReturn(accountId);
        when(dto.userId()).thenReturn(userId);
        when(dto.name()).thenReturn("Mastercard Black");
        when(dto.creditLimit()).thenReturn(new BigDecimal("10000.00"));
        when(dto.closingDay()).thenReturn(10);
        when(dto.dueDay()).thenReturn(20);
        // Nota: se o seu response expõe o saldo/limite disponível, o método 'from' vai ler este campo do DTO
        when(dto.availableLimit()).thenReturn(availableLimit);

        // Testa o segundo método 'from' que estava com 0%
        CreditCardResponse response = CreditCardResponse.from(dto);

        assertEquals(id, response.id());
        assertEquals("Mastercard Black", response.name());
        assertEquals(new BigDecimal("10000.00"), response.creditLimit());
        assertEquals(10, response.closingDay());
        assertEquals(20, response.dueDay());
    }
}