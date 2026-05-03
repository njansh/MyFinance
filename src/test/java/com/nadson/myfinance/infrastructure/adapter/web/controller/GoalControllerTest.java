package com.nadson.myfinance.infrastructure.adapter.web.controller;

import com.nadson.myfinance.application.port.in.CreateGoalPort;
import com.nadson.myfinance.domain.entity.Goal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GoalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreateGoalPort createGoalPort;

    private final String USER_ID = "550e8400-e29b-41d4-a716-446655440000";
    private final UUID USER_UUID = UUID.fromString(USER_ID);

    @Test
    @DisplayName("Should return 201 when goal is created successfully")
    void shouldCreateGoal() throws Exception {
        String description = "Viagem para Porto de Galinhas";
        BigDecimal target = new BigDecimal("5000.00");


        Goal mockGoal = new Goal(UUID.randomUUID(), USER_UUID, description, target, BigDecimal.ZERO);

        when(createGoalPort.execute(eq(USER_UUID), eq(description), any(BigDecimal.class)))
                .thenReturn(mockGoal);

        var auth = new UsernamePasswordAuthenticationToken(USER_ID, null, Collections.emptyList());

        mockMvc.perform(post("/goals")
                        .with(csrf())
                        .with(authentication(auth))
                        .param("description", description)
                        .param("targetAmount", "5000.00"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value(description))
                .andExpect(jsonPath("$.targetAmount").value(5000.00))
                .andExpect(jsonPath("$.currentAmount").value(0));
    }

    @Test
    @DisplayName("Should return 403 when creating goal without authentication")
    void shouldReturn403WhenUnauthenticated() throws Exception {
        mockMvc.perform(post("/goals")
                        .with(csrf())
                        .param("description", "Teste")
                        .param("targetAmount", "100.00"))
                .andExpect(status().isForbidden());
    }
}