package com.nadson.myfinance.infrastructure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // Injeta o segredo simulando o comportamento do @Value sem precisar subir o contexto do Spring
        ReflectionTestUtils.setField(jwtService, "secretString", "MinhaChaveSuperSecretaParaTestesLocaisMuitoLonga12345");
    }

    @Test
    void shouldGenerateValidToken() {
        String userId = "123e4567-e89b-12d3-a456-426614174000";

        String token = jwtService.generateToken(userId);

        assertNotNull(token, "O token não deveria ser nulo");
        assertFalse(token.isBlank(), "O token não deveria ser vazio");

        assertEquals(3, token.split("\\.").length, "O token JWT deve ter 3 partes estruturais");
    }
}