package com.nadson.myfinance.infrastructure.adapter.persistence.entity;

import com.nadson.myfinance.domain.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UserJpaEntityTest {
    @Test
    @DisplayName("Cobertura 100% UserJpaEntity")
    void testUser() {
        UUID id = UUID.randomUUID();
        User domain = new User(id, "Teste", "a@a.com", "pass");

        // Test Empty Constructor and Setters
        UserJpaEntity entityEmpty = new UserJpaEntity();
        entityEmpty.setId(id);
        entityEmpty.setName("Teste");
        entityEmpty.setEmail("a@a.com");
        entityEmpty.setPassword("pass");

        // Test Constructor with Domain
        UserJpaEntity entity = new UserJpaEntity(domain);

        // Test all Getters
        assertEquals(id, entity.getId());
        assertEquals("Teste", entity.getName());
        assertEquals("a@a.com", entity.getEmail());
        assertEquals("pass", entity.getPassword());

        // Test toDomain
        assertEquals(id, entity.toDomain().getId());
    }
}