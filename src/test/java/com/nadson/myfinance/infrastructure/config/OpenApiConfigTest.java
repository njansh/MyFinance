package com.nadson.myfinance.infrastructure.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OpenApiConfigTest {

    @Test
    @DisplayName("Deve cobrir a inicialização da configuração do Swagger/OpenAPI")
    void shouldInstantiateOpenApiConfig() {
        OpenApiConfig config = new OpenApiConfig();
        assertNotNull(config);
    }
}