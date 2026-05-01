package com.nadson.myfinance.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader!= null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                String userId = jwtService.extractUserId(token);
                if (userId!= null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // Cria uma autenticação usando o UUID como "Username"
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userId, null, Collections.emptyList());
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (Exception e) {
                // Token inválido, expirado ou forjado. Contexto permanece nulo (não autenticado).
            }
        }
        filterChain.doFilter(request, response);
    }
}