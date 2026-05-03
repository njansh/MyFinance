package com.nadson.myfinance.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

@Component
public class IdempotencyFilter extends OncePerRequestFilter {

    private final StringRedisTemplate redisTemplate;

    public IdempotencyFilter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String idempotencyKey = request.getHeader("Idempotency-Key");

        if (idempotencyKey!= null && request.getMethod().equalsIgnoreCase("POST")) {
            String redisKey = "idempotency:" + idempotencyKey;

            Boolean isNewRequest = redisTemplate.opsForValue().setIfAbsent(redisKey, "processing", Duration.ofHours(24));

            if (Boolean.FALSE.equals(isNewRequest)) {
                response.setStatus(409);
                response.setContentType("application/json");
                response.getWriter().write("{\"status\": 409, \"message\": \"Requisicao duplicada rejeitada pela borda do sistema.\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}