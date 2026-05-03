package com.nadson.myfinance.infrastructure.adapter.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nadson.myfinance.application.port.in.*;
import com.nadson.myfinance.domain.entity.User;
import com.nadson.myfinance.infrastructure.adapter.web.dto.request.UserRequest;
import com.nadson.myfinance.infrastructure.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean private CreateUserPort createUserPort;
    @MockBean private GetUserPort getUserPort;
    @MockBean private GetTotalBalancePort getTotalBalancePort;
    @MockBean private ListAccountsByUserPort listAccountsByUserPort;
    @MockBean private JwtService jwtService;
    @MockBean private DeleteUserPort deleteUserPort;

    private final String USER_ID = "550e8400-e29b-41d4-a716-446655440000";
    private final UUID USER_UUID = UUID.fromString(USER_ID);
    private final UsernamePasswordAuthenticationToken AUTH = new UsernamePasswordAuthenticationToken(USER_ID, null, Collections.emptyList());

    @Test
    @DisplayName("Deve criar um novo usuário e retornar 201")
    void shouldCreateUser() throws Exception {
        UserRequest request = new UserRequest("Nadson Jhony", "nadson@example.com");
        User mockUser = new User(USER_UUID, "Nadson Jhony", "nadson@example.com");

        when(createUserPort.execute(anyString(), anyString())).thenReturn(mockUser);

        mockMvc.perform(post("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Nadson Jhony"))
                .andExpect(jsonPath("$.email").value("nadson@example.com"));
    }

    @Test
    @DisplayName("Deve retornar usuário por ID")
    void shouldGetUserById() throws Exception {
        User mockUser = new User(USER_UUID, "Nadson Jhony", "nadson@example.com");
        when(getUserPort.execute(USER_UUID)).thenReturn(mockUser);

        mockMvc.perform(get("/users/{id}", USER_UUID)
                        .with(authentication(AUTH)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(USER_ID));
    }

    @Test
    @DisplayName("Deve retornar o saldo total de um usuário")
    void shouldGetTotalBalance() throws Exception {
        BigDecimal balance = new BigDecimal("15500.50");
        when(getTotalBalancePort.execute(USER_UUID)).thenReturn(balance);

        mockMvc.perform(get("/users/{id}/total-balance", USER_UUID)
                        .with(authentication(AUTH)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(15500.50));
    }

    @Test
    @DisplayName("Deve gerar token de desenvolvimento com sucesso")
    void shouldGenerateDevToken() throws Exception {
        String token = "mocked-jwt-token";
        when(jwtService.generateToken(USER_ID)).thenReturn(token);

        mockMvc.perform(get("/users/{id}/token", USER_UUID)
                        .with(authentication(AUTH)))
                .andExpect(status().isOk())
                .andExpect(content().string(token));
    }

    @Test
    @DisplayName("Deve deletar o próprio usuário e retornar 204")
    void shouldDeleteMyUser() throws Exception {
        mockMvc.perform(delete("/users/me")
                        .with(csrf())
                        .with(authentication(AUTH)))
                .andExpect(status().isNoContent());

        verify(deleteUserPort).execute(USER_UUID);
    }

    @Test
    @DisplayName("Deve retornar 400 ao tentar deletar usuário sem token devido ao parsing do UUID")
    void shouldReturn400WhenUnauthenticated() throws Exception {
        mockMvc.perform(delete("/users/me")
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }
}