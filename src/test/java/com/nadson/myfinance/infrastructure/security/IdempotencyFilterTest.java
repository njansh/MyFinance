package com.nadson.myfinance.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IdempotencyFilterTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private IdempotencyFilter filter;

    @Test
    @DisplayName("Deve permitir a requisição quando a chave de idempotência é nova")
    void shouldAllowNewRequest() throws ServletException, IOException {
        String key = "unique-key-123";
        when(request.getHeader("Idempotency-Key")).thenReturn(key);
        when(request.getMethod()).thenReturn("POST");

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(eq("idempotency:" + key), eq("processing"), any(Duration.class)))
                .thenReturn(true);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Deve retornar 409 Conflict quando a chave de idempotência já existe no Redis")
    void shouldRejectDuplicateRequest() throws ServletException, IOException {
        String key = "duplicate-key";
        when(request.getHeader("Idempotency-Key")).thenReturn(key);
        when(request.getMethod()).thenReturn("POST");

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(anyString(), anyString(), any(Duration.class)))
                .thenReturn(false); // Simula que a chave já existe

        when(response.getWriter()).thenReturn(mock(PrintWriter.class));

        filter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(409);
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("Deve ignorar a verificação se o método não for POST")
    void shouldIgnoreNonPostRequests() throws ServletException, IOException {
        when(request.getHeader("Idempotency-Key")).thenReturn("any-key");
        when(request.getMethod()).thenReturn("GET");

        filter.doFilterInternal(request, response, filterChain);

        verify(redisTemplate, never()).opsForValue();
        verify(filterChain).doFilter(request, response);
    }
}