package com.nadson.myfinance.infrastructure.adapter.web.controller;

import com.nadson.myfinance.application.port.in.CreateBudgetPort;
import com.nadson.myfinance.domain.entity.Budget;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BudgetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreateBudgetPort createBudgetPort;

    @Test
    @DisplayName("Should return 403 when creating budget without token")
    void shouldReturn403WhenUnauthorized() throws Exception {
        mockMvc.perform(post("/budgets")
                        .with(csrf())
                        .param("categoryId", UUID.randomUUID().toString())
                        .param("month", "5")
                        .param("year", "2026")
                        .param("limitAmount", "1000"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return 201 when budget is created successfully")
    void shouldReturn201WhenCreated() throws Exception {
        UUID categoryId = UUID.randomUUID();
        String userStr = "550e8400-e29b-41d4-a716-446655440000";
        UUID userUuid = UUID.fromString(userStr);
        BigDecimal amount = new BigDecimal("1000.00");

        Budget mockBudget = new Budget(UUID.randomUUID(), userUuid, categoryId, 5, 2026, amount);

        when(createBudgetPort.execute(eq(userUuid), eq(categoryId), eq(5), eq(2026), any(BigDecimal.class)))
                .thenReturn(mockBudget);

        var auth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                userStr, null, java.util.Collections.emptyList());

        mockMvc.perform(post("/budgets")
                        .with(csrf())
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication(auth))
                        .param("categoryId", categoryId.toString())
                        .param("month", "5")
                        .param("year", "2026")
                        .param("limitAmount", "1000.00"))
                .andExpect(status().isCreated());
    }
}