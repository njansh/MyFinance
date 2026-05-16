package com.nadson.myfinance.infrastructure.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.Optional;

@Service
public class JwtCookieService {

    public Cookie createAccessTokenCookie(String accessToken) {
        Cookie cookie = new Cookie("accessToken", accessToken);
        cookie.setHttpOnly(true);
        // cookie.setSecure(true); // Habilitar em produção quando tiver HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24); // Expira em 1 dia
        cookie.setAttribute("SameSite", "Lax"); // Mitiga ataques CSRF
        return cookie;
    }

    public Cookie createRefreshTokenCookie(String refreshToken) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        // cookie.setSecure(true);
        cookie.setPath("/api/auth/refresh");
        cookie.setMaxAge(7 * 24 * 60 * 60); // Expira em 7 dias
        cookie.setAttribute("SameSite", "Strict");
        return cookie;
    }

    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return Optional.empty();
        }
        return Arrays.stream(request.getCookies())
                .filter(c -> "refreshToken".equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }
}