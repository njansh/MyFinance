package com.nadson.myfinance.infrastructure.adapter.web.controller;

import com.nadson.myfinance.application.port.in.GetFinancialDashboardKpisPort;
import com.nadson.myfinance.infrastructure.adapter.web.dto.response.KpiDashboardResponse;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetFinancialDashboardKpisPort kpisPort;

    private final String USER_ID = "550e8400-e29b-41d4-a716-446655440000";
    private final UUID USER_UUID = UUID.fromString(USER_ID);

    @Test
    @DisplayName("Should return 200 and KPIs when requested with valid parameters")
    void shouldReturnDashboardKpis() throws Exception {
        // Criamos um mock de resposta baseado nos campos reais do seu Record
        KpiDashboardResponse mockResponse = new KpiDashboardResponse(
                new BigDecimal("5000.00"), // netWorth
                new BigDecimal("3000.00"), // monthlyIncome
                new BigDecimal("2000.00"), // monthlyExpense
                new BigDecimal("1000.00"), // cashFlow
                new BigDecimal("0.80")     // savingsRatio
        );

        when(kpisPort.execute(eq(USER_UUID), eq(5), eq(2026)))
                .thenReturn(mockResponse);

        var auth = new UsernamePasswordAuthenticationToken(USER_ID, null, Collections.emptyList());

        mockMvc.perform(get("/api/dashboard/kpis")
                        .with(authentication(auth))
                        .param("month", "5")
                        .param("year", "2026"))
                .andExpect(status().isOk())
                // AJUSTE: Nomes devem ser idênticos aos do record KpiDashboardResponse
                .andExpect(jsonPath("$.netWorth").value(5000.00))
                .andExpect(jsonPath("$.monthlyIncome").value(3000.00))
                .andExpect(jsonPath("$.monthlyExpense").value(2000.00))
                .andExpect(jsonPath("$.cashFlow").value(1000.00))
                .andExpect(jsonPath("$.savingsRatio").value(0.80));
    }
    @Test
    @DisplayName("Should return 403 when accessing dashboard without token")
    void shouldReturn403WhenUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/dashboard/kpis"))
                .andExpect(status().isForbidden());
    }
}
