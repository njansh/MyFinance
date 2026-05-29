package com.nadson.myfinance.infrastructure.adapter.persistence.entity;

import com.nadson.myfinance.domain.entity.RecurringTemplate;
import com.nadson.myfinance.domain.enums.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecurringTemplateJpaEntityTest {

    @Test
    @DisplayName("Cobertura 100% RecurringTemplateJpaEntity")
    void testRecurring() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID accId = UUID.randomUUID();
        UUID catId = UUID.randomUUID();

        RecurringTemplate domain = new RecurringTemplate(id, userId, accId, catId, "Ref", BigDecimal.TEN, TransactionType.EXPENSE, 1, true);

        // 1. Testar Construtor vazio e TODOS os Setters
        RecurringTemplateJpaEntity entityEmpty = new RecurringTemplateJpaEntity();
        entityEmpty.setId(id);
        entityEmpty.setUserId(userId);
        entityEmpty.setAccountId(accId);
        entityEmpty.setCategoryId(catId);
        entityEmpty.setDescription("Ref");
        entityEmpty.setExpectedAmount(BigDecimal.TEN);
        entityEmpty.setType(TransactionType.EXPENSE);
        entityEmpty.setFrequencyDay(1);
        entityEmpty.setActive(true);
        entityEmpty.setLastExecutedMonth(1);
        entityEmpty.setLastExecutedYear(2026);

        // 2. Validar todos os Getters em cima da entidade que acabamos de preencher
        assertEquals(id, entityEmpty.getId());
        assertEquals(userId, entityEmpty.getUserId());
        assertEquals(accId, entityEmpty.getAccountId());
        assertEquals(catId, entityEmpty.getCategoryId());
        assertEquals("Ref", entityEmpty.getDescription());
        assertEquals(BigDecimal.TEN, entityEmpty.getExpectedAmount());
        assertEquals(TransactionType.EXPENSE, entityEmpty.getType());
        assertEquals(1, entityEmpty.getFrequencyDay());
        assertTrue(entityEmpty.isActive());
        assertEquals(1, entityEmpty.getLastExecutedMonth());
        assertEquals(2026, entityEmpty.getLastExecutedYear());

        // 3. Testar conversores (fromDomain e toDomain) para garantir a cobertura dessas linhas
        RecurringTemplateJpaEntity entity = RecurringTemplateJpaEntity.fromDomain(domain);
        assertEquals(id, entity.toDomain().getId());
    }
}