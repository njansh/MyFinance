package com.nadson.myfinance.infrastructure.adapter.web.controller;

import com.nadson.myfinance.application.port.in.GetFinancialDashboardKpisPort;
import com.nadson.myfinance.infrastructure.adapter.web.dto.response.KpiDashboardResponse;
import com.nadson.myfinance.infrastructure.security.WithMockUserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DashboardController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com.nadson.myfinance.infrastructure.security.*"))
@WithMockUserId
@AutoConfigureMockMvc(addFilters = false)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetFinancialDashboardKpisPort kpisPort;

    @Test
    @DisplayName("Deve retornar KPIs do dashboard com sucesso")
    void shouldGetKpisSuccessfully() throws Exception {
        UUID userId = UUID.fromString("d6e3e5b0-1234-4321-8765-abcdef123456");
        KpiDashboardResponse response = new KpiDashboardResponse(
                new BigDecimal("5000.00"),
                new BigDecimal("3000.00"),
                new BigDecimal("2000.00"),
                new BigDecimal("1000.00"),
                new BigDecimal("0.80")
        );

        when(kpisPort.execute(eq(userId), eq(5), eq(2026)))
                .thenReturn(response);

        mockMvc.perform(get("/api/dashboard/kpis")
                        .param("month", "5")
                        .param("year", "2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.netWorth").value(5000.00))
                .andExpect(jsonPath("$.monthlyIncome").value(3000.00))
                .andExpect(jsonPath("$.monthlyExpense").value(2000.00))
                .andExpect(jsonPath("$.lastMonthBalance").value(1000.00))
                .andExpect(jsonPath("$.nextMonthForecast").value(0.80));
    }
}