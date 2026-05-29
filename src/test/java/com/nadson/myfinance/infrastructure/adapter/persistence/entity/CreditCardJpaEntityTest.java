package com.nadson.myfinance.infrastructure.adapter.persistence.entity;

import com.nadson.myfinance.domain.entity.CreditCard;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CreditCardJpaEntityTest {
    @Test
    @DisplayName("Deve cobrir 100% da entidade CreditCardJpaEntity")
    void shouldCoverCreditCardJpaEntity() {
        UUID id = UUID.randomUUID();
        UUID accId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        CreditCard domain = new CreditCard(id, accId, userId, "Nubank", BigDecimal.TEN, 1, 10);

        CreditCardJpaEntity entity = new CreditCardJpaEntity(domain);
        entity.setId(id);
        entity.setAccountId(accId);
        entity.setUserId(userId);
        entity.setName("Nubank");
        entity.setClosingDay(1);
        entity.setDueDay(10);

        assertEquals(id, entity.getId());
        assertEquals(accId, entity.getAccountId());
        assertEquals(userId, entity.getUserId());
        assertEquals("Nubank", entity.getName());
        assertEquals(BigDecimal.TEN, entity.getCreditLimit());
        assertEquals(1, entity.getClosingDay());
        assertEquals(10, entity.getDueDay());
        assertEquals(id, entity.toDomain().getId());
    }
    @Test
    @DisplayName("Deve cobrir o construtor vazio e os getters/setters base")
    void shouldCoverEmptyConstructor() {
        CreditCardJpaEntity entity = new CreditCardJpaEntity();

        UUID id = UUID.randomUUID();
        entity.setId(id);
        entity.setAccountId(id);
        entity.setUserId(id);
        entity.setName("Teste Vazio");
        entity.setCreditLimit(BigDecimal.ONE);
        entity.setClosingDay(1);
        entity.setDueDay(5);

        assertEquals(id, entity.getId());
        assertEquals("Teste Vazio", entity.getName());
        assertEquals(1, entity.getClosingDay());
        assertEquals(5, entity.getDueDay());
    }
}