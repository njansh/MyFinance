package com.nadson.myfinance.infrastructure.adapter.web.controller;

import com.nadson.myfinance.infrastructure.security.JwtCookieService;
import com.nadson.myfinance.infrastructure.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtService jwtService;
    private final JwtCookieService cookieService;

    public AuthController(JwtService jwtService, JwtCookieService cookieService) {
        this.jwtService = jwtService;
        this.cookieService = cookieService;
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        Optional<String> refreshTokenOpt = cookieService.extractRefreshToken(request);

        if (refreshTokenOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Refresh token ausente no cookie"));
        }

        try {
            String userId = jwtService.extractUserId(refreshTokenOpt.get());

            String newAccessToken = jwtService.generateToken(userId);

            return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Refresh token inválido ou expirado"));
        }
    }
}