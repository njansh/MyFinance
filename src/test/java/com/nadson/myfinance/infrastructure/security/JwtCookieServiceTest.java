package com.nadson.myfinance.infrastructure.security;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseCookie;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class JwtCookieServiceTest {

    private final JwtCookieService jwtCookieService = new JwtCookieService();

    @Test
    @DisplayName("Deve criar o cookie de Access Token corretamente")
    void shouldCreateAccessTokenCookie() {
        Cookie cookie = jwtCookieService.createAccessTokenCookie("access-token-123");

        assertEquals("accessToken", cookie.getName());
        assertEquals("access-token-123", cookie.getValue());
        assertTrue(cookie.isHttpOnly());
    }

    @Test
    @DisplayName("Deve criar o cookie de Refresh Token corretamente")
    void shouldCreateRefreshTokenCookie() {
        Cookie cookie = jwtCookieService.createRefreshTokenCookie("refresh-token-123");

        assertEquals("refreshToken", cookie.getName());
        assertEquals("refresh-token-123", cookie.getValue());
        assertTrue(cookie.isHttpOnly());
    }

    @Test
    @DisplayName("Deve extrair o refresh token do request com sucesso")
    void shouldExtractRefreshToken() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("refreshToken", "my-refresh-token"));

        Optional<String> extracted = jwtCookieService.extractRefreshToken(request);

        assertTrue(extracted.isPresent());
        assertEquals("my-refresh-token", extracted.get());
    }

    @Test
    @DisplayName("Deve retornar Optional.empty ao extrair token se não houver cookies")
    void shouldReturnEmptyWhenExtractingWithoutCookies() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        Optional<String> extracted = jwtCookieService.extractRefreshToken(request);

        assertTrue(extracted.isEmpty());
    }
}