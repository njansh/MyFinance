package com.nadson.myfinance.infrastructure.adapter.persistence.entity;

import com.nadson.myfinance.domain.entity.CreditCardPurchase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CreditCardPurchaseJpaEntityTest {
    @Test
    @DisplayName("Deve cobrir 100% da entidade CreditCardPurchaseJpaEntity")
    void shouldCoverCreditCardPurchaseJpaEntity() {
        UUID id = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();
        UUID catId = UUID.randomUUID();
        LocalDate now = LocalDate.now();
        CreditCardPurchase domain = new CreditCardPurchase(id, cardId, catId, "Compra", BigDecimal.TEN, 1, now);

        // Test Empty Constructor and all Setters
        CreditCardPurchaseJpaEntity entityEmpty = new CreditCardPurchaseJpaEntity();
        entityEmpty.setId(id);
        entityEmpty.setCreditCardId(cardId);
        entityEmpty.setCategoryId(catId);
        entityEmpty.setDescription("Compra");
        entityEmpty.setTotalAmount(BigDecimal.TEN);
        entityEmpty.setTotalInstallments(1);
        entityEmpty.setPurchaseDate(now);

        // Test Constructor with Domain
        CreditCardPurchaseJpaEntity entity = new CreditCardPurchaseJpaEntity(domain);

        // Test all Getters
        assertEquals(id, entity.getId());
        assertEquals(cardId, entity.getCreditCardId());
        assertEquals(catId, entity.getCategoryId());
        assertEquals("Compra", entity.getDescription());
        assertEquals(BigDecimal.TEN, entity.getTotalAmount());
        assertEquals(1, entity.getTotalInstallments());
        assertEquals(now, entity.getPurchaseDate());

        // Test toDomain
        assertEquals(id, entity.toDomain().getId());
    }
}