package com.nadson.myfinance.infrastructure.adapter.web.controller;

import com.nadson.myfinance.application.port.in.CreateTransactionPort;
import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.domain.enums.TransactionType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreateTransactionPort createTransactionPort;

    @Test
    void shouldReturn403WhenAccessingWithoutToken() throws Exception {
        mockMvc.perform(post("/transactions")
                        .with(csrf()) // Previne bloqueio por CSRF
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void shouldReturn400WhenIdempotencyKeyIsMissing() throws Exception {
        mockMvc.perform(post("/transactions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\": \"Teste\", \"amount\": 100}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void shouldReturn201WhenTransactionIsCreatedSuccessfully() throws Exception {
        UUID accountId = UUID.randomUUID();

        // Criamos um objeto de retorno para o Mock não devolver null
        Transaction mockOutput = new Transaction(
                UUID.randomUUID(), "Teste", new BigDecimal("100.00"),
                LocalDateTime.now(), TransactionType.EXPENSE, accountId, null, false, null, null
        );

        // Ensinamos o MockBean a retornar o objeto acima [1, 2]
        when(createTransactionPort.execute(any(Transaction.class))).thenReturn(mockOutput);

        String validBody = """
                {
                    "description": "Compra Teste",
                    "amount": 150.0,
                    "type": "EXPENSE",
                    "accountId": "%s",
                    "date": "2026-05-03T10:00:00",
                    "isTransfer": false
                }
                """.formatted(accountId);

        mockMvc.perform(post("/transactions")
                        .header("Idempotency-Key", UUID.randomUUID().toString())
                        .with(csrf()) // Previne o erro 403 de CSRF [3]
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBody))
                .andExpect(status().isCreated());
    }
}