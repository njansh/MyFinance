package com.nadson.myfinance.infrastructure.adapter.web.controller;

import com.nadson.myfinance.application.port.in.*;
import com.nadson.myfinance.domain.entity.Budget;
import com.nadson.myfinance.infrastructure.adapter.web.dto.request.BudgetRequest;
import com.nadson.myfinance.infrastructure.security.WithMockUserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BudgetController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com.nadson.myfinance.infrastructure.security.*"))
@WithMockUserId
@AutoConfigureMockMvc(addFilters = false)
class BudgetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private CreateBudgetPort createBudgetPort;
    @MockBean private ListBudgetsPort listBudgetsPort;
    @MockBean private UpdateBudgetLimitPort updateBudgetLimitPort;
    @MockBean private DeleteBudgetPort deleteBudgetPort;
    @MockBean private GetBudgetPort getBudgetPort;

    @Test
    @DisplayName("Deve criar orçamento com sucesso")
    void shouldCreateBudget() throws Exception {
        UUID userId = UUID.fromString("d6e3e5b0-1234-4321-8765-abcdef123456");
        Budget budget = new Budget(UUID.randomUUID(), userId, UUID.randomUUID(), 5, 2026, BigDecimal.TEN);

        when(createBudgetPort.execute(eq(userId), any(), eq(5), eq(2026), eq(BigDecimal.TEN)))
                .thenReturn(budget);

        String json = """
            {"categoryId": "%s", "month": 5, "year": 2026, "limitAmount": 10}
            """.formatted(UUID.randomUUID());

        mockMvc.perform(post("/budgets")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Deve listar orçamentos por mês e ano")
    void shouldListBudgets() throws Exception {
        when(listBudgetsPort.execute(any(), eq(5), eq(2026)))
                .thenReturn(List.of());

        mockMvc.perform(get("/budgets")
                        .param("month", "5")
                        .param("year", "2026"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve deletar orçamento")
    void shouldDeleteBudget() throws Exception {
        UUID id = UUID.randomUUID();
        mockMvc.perform(delete("/budgets/" + id).with(csrf()))
                .andExpect(status().isNoContent());
    }
    @Test
    @DisplayName("Deve buscar orçamento por ID com sucesso")
    void shouldGetBudgetById() throws Exception {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.fromString("d6e3e5b0-1234-4321-8765-abcdef123456");
        Budget budget = new Budget(id, userId, UUID.randomUUID(), 5, 2026, BigDecimal.TEN);

        when(getBudgetPort.execute(id)).thenReturn(budget);

        mockMvc.perform(get("/budgets/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.limitAmount").value(10));
    }

    @Test
    @DisplayName("Deve atualizar o limite do orçamento")
    void shouldUpdateBudgetLimit() throws Exception {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.fromString("d6e3e5b0-1234-4321-8765-abcdef123456");
        BigDecimal newLimit = BigDecimal.valueOf(500);
        Budget budget = new Budget(id, userId, UUID.randomUUID(), 5, 2026, newLimit);

        when(updateBudgetLimitPort.execute(eq(userId), eq(id), eq(newLimit)))
                .thenReturn(budget);

        mockMvc.perform(patch("/budgets/" + id + "/limit")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("500"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.limitAmount").value(500));
    }
}