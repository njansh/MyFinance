package com.nadson.myfinance.infrastructure.adapter.persistence.entity;

import com.nadson.myfinance.domain.entity.Budget;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BudgetJpaEntityTest {

    @Test
    @DisplayName("Deve cobrir 100% da entidade BudgetJpaEntity")
    void shouldCoverBudgetJpaEntity() {
        // Setup
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        int month = 5;
        int year = 2026;
        BigDecimal limit = new BigDecimal("2000.00");
        BigDecimal spent = new BigDecimal("500.00");

        Budget domain = new Budget(id, userId, categoryId, month, year, limit, spent, false, false);

        // 1. Testar construtor vazio e setters
        BudgetJpaEntity entity = new BudgetJpaEntity();
        entity.setId(id);
        entity.setUserId(userId);
        entity.setCategoryId(categoryId);
        entity.setMonth(month);
        entity.setYear(year);
        entity.setLimitAmount(limit);
        entity.setSpentAmount(spent);
        entity.setAlertedEightyPercent(true);
        entity.setAlertedOneHundredPercent(true);

        // 2. Testar construtor com domínio
        BudgetJpaEntity entityFromDomain = new BudgetJpaEntity(domain);

        // 3. Testar toDomain
        Budget result = entityFromDomain.toDomain();

        // 4. Asserts para getters e validadores
        assertEquals(id, entity.getId());
        assertEquals(userId, entity.getUserId());
        assertEquals(categoryId, entity.getCategoryId());
        assertEquals(month, entity.getMonth());
        assertEquals(year, entity.getYear());
        assertEquals(limit, entity.getLimitAmount());
        assertEquals(spent, entity.getSpentAmount());
        assertTrue(entity.isAlertedEightyPercent());
        assertTrue(entity.isAlertedOneHundredPercent());

        // Assert do domínio convertido
        assertEquals(id, result.getId());
        assertEquals(limit, result.getLimitAmount());
    }
}