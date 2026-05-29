package com.nadson.myfinance.infrastructure.adapter.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nadson.myfinance.application.port.in.*;
import com.nadson.myfinance.domain.entity.RecurringTemplate;
import com.nadson.myfinance.domain.enums.TransactionType;
import com.nadson.myfinance.infrastructure.adapter.web.dto.request.CreateRecurringTemplateRequest;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RecurringController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com.nadson.myfinance.infrastructure.security.*"))
@WithMockUserId
@AutoConfigureMockMvc(addFilters = false)
class RecurringControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private ListPendingRecurringPort listPendingRecurringPort;
    @MockBean private CreateRecurringTemplatePort createRecurringTemplatePort;
    @MockBean private ListRecurringTemplatesPort listRecurringTemplatesPort;
    @MockBean private DeleteRecurringTemplatePort deleteRecurringTemplatePort;

    @Test
    @DisplayName("Deve criar modelo de recorrencia com sucesso")
    void shouldCreateRecurringTemplate() throws Exception {
        CreateRecurringTemplateRequest request = new CreateRecurringTemplateRequest(
                "Aluguel", new BigDecimal("1200.00"), TransactionType.EXPENSE,UUID.randomUUID(), UUID.randomUUID(),5
        );

        when(createRecurringTemplatePort.execute(any(RecurringTemplate.class)))
                .thenReturn(mock(RecurringTemplate.class));

        mockMvc.perform(post("/recurring")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Deve listar templates de recorrencia")
    void shouldListTemplates() throws Exception {
        when(listRecurringTemplatesPort.execute(any(UUID.class))).thenReturn(List.of());

        mockMvc.perform(get("/recurring")
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve deletar template de recorrencia")
    void shouldDeleteTemplate() throws Exception {
        UUID id = UUID.randomUUID();
        mockMvc.perform(delete("/recurring/" + id)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve listar transações recorrentes pendentes")
    void shouldGetPendingTransactions() throws Exception {
        when(listPendingRecurringPort.execute(any(UUID.class), any(Integer.class), any(Integer.class)))
                .thenReturn(List.of());

        mockMvc.perform(get("/recurring/pending")
                        .param("month", "5")
                        .param("year", "2026"))
                .andExpect(status().isOk());
    }
    @Test
    @DisplayName("Deve listar transações pendentes usando valores default para mês e ano")
    void shouldGetPendingTransactionsWithDefaultDates() throws Exception {
        when(listPendingRecurringPort.execute(any(UUID.class), any(Integer.class), any(Integer.class)))
                .thenReturn(List.of());

        mockMvc.perform(get("/recurring/pending")
                        .with(csrf()))
                .andExpect(status().isOk());
    }
}