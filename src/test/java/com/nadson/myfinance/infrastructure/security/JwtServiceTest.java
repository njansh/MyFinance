package com.nadson.myfinance.infrastructure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private final String secret = "minha-chave-secreta-muito-longa-e-segura-com-pelo-menos-256-bits";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretString", secret);
    }

    @Test
    @DisplayName("Deve gerar um token JWT válido contendo o ID do usuário")
    void shouldGenerateValidToken() {
        String userId = UUID.randomUUID().toString();

        String token = jwtService.generateToken(userId);

        assertNotNull(token);
        assertFalse(token.isEmpty());

        String extractedId = jwtService.extractUserId(token);
        assertEquals(userId, extractedId);
    }

    @Test
    @DisplayName("Deve extrair corretamente o User ID de um token existente")
    void shouldExtractUserIdFromToken() {
        String userId = "user-123";
        String token = jwtService.generateToken(userId);

        String result = jwtService.extractUserId(token);

        assertEquals(userId, result);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar extrair dados de um token inválido ou alterado")
    void shouldThrowExceptionForInvalidToken() {
        String invalidToken = "token.invalido.assinatura-errada";

        assertThrows(Exception.class, () -> jwtService.extractUserId(invalidToken));
    }
}