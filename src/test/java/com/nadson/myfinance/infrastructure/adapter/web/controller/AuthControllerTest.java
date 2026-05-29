package com.nadson.myfinance.infrastructure.adapter.web.controller;

import com.nadson.myfinance.infrastructure.security.JwtCookieService;
import com.nadson.myfinance.infrastructure.security.JwtService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private JwtService jwtService;
    @MockBean private JwtCookieService cookieService;
    @MockBean private AuthenticationManager authenticationManager;
    @MockBean private org.springframework.data.redis.core.StringRedisTemplate stringRedisTemplate;
    @Test
    @DisplayName("Deve realizar login e retornar cookies com sucesso")
    void shouldLoginSuccessfully() throws Exception {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user-id-123");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(jwtService.generateToken("user-id-123")).thenReturn("mocked-token");
        when(cookieService.createAccessTokenCookie(any())).thenReturn(new Cookie("access_token", "mocked-access"));
        when(cookieService.createRefreshTokenCookie(any())).thenReturn(new Cookie("refresh_token", "mocked-refresh"));

        String json = """
            {"email": "teste@email.com", "password": "123"}
            """;

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login realizado com sucesso"))
                .andExpect(cookie().exists("access_token"))
                .andExpect(cookie().exists("refresh_token"));
    }

    @Test
    @DisplayName("Deve renovar token com sucesso quando refresh token for válido")
    void shouldRefreshTokenSuccessfully() throws Exception {
        String validRefreshToken = "valid-token";

        when(cookieService.extractRefreshToken(any())).thenReturn(Optional.of(validRefreshToken));
        when(jwtService.extractUserId(validRefreshToken)).thenReturn("user-id-123");
        when(jwtService.generateToken("user-id-123")).thenReturn("new-access-token");
        when(cookieService.createAccessTokenCookie("new-access-token"))
                .thenReturn(new Cookie("access_token", "new-access-token"));

        mockMvc.perform(post("/api/auth/refresh")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Token renovado com sucesso"))
                .andExpect(cookie().exists("access_token"));
    }

    @Test
    @DisplayName("Deve retornar 401 quando o refresh token não estiver presente")
    void shouldFailRefreshWhenTokenMissing() throws Exception {
        when(cookieService.extractRefreshToken(any())).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/auth/refresh")
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Refresh token ausente no cookie"));
    }

    @Test
    @DisplayName("Deve retornar 401 quando o refresh token for inválido ou expirado")
    void shouldFailRefreshWhenTokenIsInvalid() throws Exception {
        String invalidToken = "bad-token";
        when(cookieService.extractRefreshToken(any())).thenReturn(Optional.of(invalidToken));
        // Simula exceção na validação (cobrindo o bloco catch)
        when(jwtService.extractUserId(invalidToken)).thenThrow(new RuntimeException("Erro"));

        mockMvc.perform(post("/api/auth/refresh")
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Refresh token inválido ou expirado"));
    }



}