package com.nadson.myfinance.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "MyFinance API", version = "1.0", description = "API de Gestão Financeira"),
        security = @SecurityRequirement(name = "bearerAuth") // Exige o token em todos os endpoints visualmente
)
@SecurityScheme(
        name = "bearerAuth",
        description = "Cole o token JWT gerado na rota GET /users/{id}/token",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}