package com.nadson.myfinance.infrastructure.adapter.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nadson.myfinance.application.port.in.CreateGoalPort;
import com.nadson.myfinance.application.port.in.DeleteGoalPort;
import com.nadson.myfinance.application.port.in.ListGoalsPort;
import com.nadson.myfinance.application.port.in.UpdateGoalPort;
import com.nadson.myfinance.domain.entity.Goal;
import com.nadson.myfinance.infrastructure.adapter.web.dto.request.CreateGoalRequest;
import com.nadson.myfinance.infrastructure.adapter.web.dto.request.UpdateGoalRequest;
import com.nadson.myfinance.infrastructure.security.JwtService; // <-- Importação do JwtService
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = GoalController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@AutoConfigureMockMvc(addFilters = false)
class GoalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // --- Mocks de Infraestrutura e Segurança ---
    @MockBean
    private StringRedisTemplate stringRedisTemplate;

    @MockBean
    private JwtService jwtService; // <-- Mock para satisfazer o JwtAuthenticationFilter

    // --- Mocks de Negócio ---
    @MockBean private CreateGoalPort createGoalPort;
    @MockBean private ListGoalsPort listGoalsPort;
    @MockBean private UpdateGoalPort updateGoalPort;
    @MockBean private DeleteGoalPort deleteGoalPort;

    private final String userId = UUID.randomUUID().toString();

    @BeforeEach
    void setup() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList())
        );
    }

    @Test
    @DisplayName("POST /goals should return 201 when goal is created")
    void shouldReturn201WhenCreatingGoal() throws Exception {
        CreateGoalRequest request = new CreateGoalRequest("Viagem", new BigDecimal("1000.00"), Collections.emptyList());

        when(createGoalPort.execute(eq(UUID.fromString(userId)), any(), any(), any()))
                .thenReturn(new Goal(UUID.randomUUID(), UUID.fromString(userId), "Viagem", BigDecimal.TEN, BigDecimal.ZERO, null));

        mockMvc.perform(post("/goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("PUT /goals/{id} should return 200 when goal is updated")
    void shouldReturn200WhenUpdatingGoal() throws Exception {
        UUID goalId = UUID.randomUUID();
        UpdateGoalRequest request = new UpdateGoalRequest("Viagem Editada", new BigDecimal("2000.00"), Collections.emptyList());

        when(updateGoalPort.execute(eq(UUID.fromString(userId)), eq(goalId), any(), any(), any()))
                .thenReturn(new Goal(goalId, UUID.fromString(userId), "Viagem Editada", new BigDecimal("2000.00"), BigDecimal.ZERO, null));

        mockMvc.perform(put("/goals/{id}", goalId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /goals should return 200")
    void shouldReturn200WhenListingGoals() throws Exception {
        mockMvc.perform(get("/goals"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /goals/{id} should return 204")
    void shouldReturn204WhenDeletingGoal() throws Exception {
        UUID goalId = UUID.randomUUID();

        mockMvc.perform(delete("/goals/{id}", goalId))
                .andExpect(status().isNoContent());
    }
}