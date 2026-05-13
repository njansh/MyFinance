package com.nadson.myfinance.infrastructure.adapter.web.controller;

import com.nadson.myfinance.infrastructure.adapter.web.dto.request.LoginRequest;
import com.nadson.myfinance.infrastructure.security.JwtCookieService;
import com.nadson.myfinance.infrastructure.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtService jwtService;
    private final JwtCookieService cookieService;
    private final AuthenticationManager authenticationManager;

    public AuthController(JwtService jwtService, JwtCookieService cookieService, AuthenticationManager authenticationManager) {
        this.jwtService = jwtService;
        this.cookieService = cookieService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        String userId = authentication.getName();

        String accessToken = jwtService.generateToken(userId);
        String refreshToken = jwtService.generateToken(userId); // Neste MVP usamos a mesma mecânica para o refresh

        response.addCookie(cookieService.createRefreshTokenCookie(refreshToken));

        return ResponseEntity.ok(Map.of("accessToken", accessToken));
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