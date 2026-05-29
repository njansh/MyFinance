package com.nadson.myfinance.infrastructure.adapter.persistence.entity;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuditRevisionListenerTest {

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Deve setar o userId do contexto de segurança na revisão")
    void shouldSetUserIdFromSecurityContext() {
        // Arrange
        AuditRevisionListener listener = new AuditRevisionListener();
        AuditRevisionEntity entity = new AuditRevisionEntity();
        String userId = "user-123";

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList())
        );

        // Act
        listener.newRevision(entity);

        // Assert
        assertEquals(userId, entity.getUserId());
    }

    @Test
    @DisplayName("Deve setar 'SYSTEM' quando não houver usuário autenticado")
    void shouldSetSystemWhenNoAuth() {
        // Arrange
        AuditRevisionListener listener = new AuditRevisionListener();
        AuditRevisionEntity entity = new AuditRevisionEntity();
        SecurityContextHolder.clearContext(); // Garante contexto vazio

        // Act
        listener.newRevision(entity);

        // Assert
        assertEquals("SYSTEM", entity.getUserId());
    }
}