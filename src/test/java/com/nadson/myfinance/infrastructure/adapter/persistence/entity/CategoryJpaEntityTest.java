package com.nadson.myfinance.infrastructure.adapter.persistence.entity;

import com.nadson.myfinance.domain.entity.Category;
import com.nadson.myfinance.domain.enums.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CategoryJpaEntityTest {

    @Test
    @DisplayName("Deve cobrir 100% da entidade CategoryJpaEntity")
    void shouldCoverCategoryJpaEntity() {
        // Setup
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String name = "Alimentação";
        String color = "#FFFFFF";
        String icon = "food-icon";
        TransactionType type = TransactionType.EXPENSE;

        Category domain = new Category(id, userId, name, color, icon, type);

        // 1. Construtor vazio e setters
        CategoryJpaEntity entity = new CategoryJpaEntity();
        entity.setId(id);
        entity.setUserId(userId);
        entity.setName(name);
        entity.setColor(color);
        entity.setIcon(icon);
        entity.setType(type);

        // 2. Construtor completo e Construtor com domínio
        CategoryJpaEntity entityFull = new CategoryJpaEntity(id, userId, name, color, icon, type);
        CategoryJpaEntity entityFromDomain = new CategoryJpaEntity(domain);

        // 3. ToDomain
        Category result = entityFromDomain.toDomain();

        // 4. Assertivas
        assertEquals(id, entity.getId());
        assertEquals(userId, entity.getUserId());
        assertEquals(name, entity.getName());
        assertEquals(color, entity.getColor());
        assertEquals(icon, entity.getIcon());
        assertEquals(type, entity.getType());

        assertEquals(name, result.getName());
        assertEquals(type, result.getType());
    }
}