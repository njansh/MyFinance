package com.nadson.myfinance.infrastructure.adapter.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nadson.myfinance.application.port.in.*;
import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.domain.enums.TransactionType;
import com.nadson.myfinance.infrastructure.adapter.web.dto.request.TransactionRequest;
import com.nadson.myfinance.infrastructure.adapter.web.dto.request.TransferRequest;
import com.nadson.myfinance.infrastructure.adapter.web.dto.response.BalanceResponse;
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
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean private CreateTransactionPort createTransactionPort;
    @MockBean private GetTransactionPort getTransactionPort;
    @MockBean private GetExpensesByCategoryPort getExpensesByCategoryPort;
    @MockBean private UpdateTransactionPort updateTransactionPort;
    @MockBean private DeleteTransactionPort deleteTransactionPort;
    @MockBean private GetAccountBalancePort getAccountBalancePort;
    @MockBean private GetIncomesByCategoryPort getIncomesByCategoryPort;
    @MockBean private TransferPort transferPort;

    private final String USER_ID = "550e8400-e29b-41d4-a716-446655440000";
    private final UsernamePasswordAuthenticationToken AUTH = new UsernamePasswordAuthenticationToken(USER_ID, null, Collections.emptyList());

    @Test
    @DisplayName("Should create transaction and return 201 using 9-param DTO")
    void shouldCreateTransaction() throws Exception {
        // Seguindo a ordem do seu record: description, amount, date, type, accountId, categoryId, transferID, isTransfer, accountBalanceAfter
        TransactionRequest request = new TransactionRequest(
                "Compra Supermercado",
                new BigDecimal("150.00"),
                LocalDateTime.now(),
                TransactionType.EXPENSE,
                UUID.randomUUID(),
                UUID.randomUUID(),
                null,
                false,
                new BigDecimal("850.00")
        );

        Transaction mockTransaction = new Transaction(
                UUID.randomUUID(), "Compra Supermercado", new BigDecimal("150.00"), LocalDateTime.now(),
                TransactionType.EXPENSE, request.accountId(), request.categoryId(),
                false, null, new BigDecimal("850.00")
        );

        when(createTransactionPort.execute(any(Transaction.class))).thenReturn(mockTransaction);

        mockMvc.perform(post("/transactions")
                        .with(csrf()).with(authentication(AUTH))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value("Compra Supermercado"));
    }

    @Test
    @DisplayName("Should execute transfer between two different accounts")
    void shouldTransferRobustly() throws Exception {
        UUID accountOrigin = UUID.randomUUID();
        UUID accountDestination = UUID.randomUUID();
        BigDecimal transferValue = new BigDecimal("500.00");

        // DTO de Transferência (TransferRequest costuma ser diferente do TransactionRequest)
        TransferRequest transferRequest = new TransferRequest(
                accountOrigin,
                accountDestination,
                transferValue,
                LocalDateTime.now()
        );

        mockMvc.perform(post("/transactions/transfer")
                        .with(csrf()).with(authentication(AUTH))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isNoContent());

        // Verificação robusta: garante que o dinheiro saiu de A e foi para B
        verify(transferPort).execute(
                eq(accountOrigin),
                eq(accountDestination),
                eq(transferValue),
                any(LocalDateTime.class),
                eq("Transferência manual"),
                any(),
                any()
        );
    }

    @Test
    @DisplayName("Should return 200 and balance for a specific account")
    void shouldGetBalanceReport() throws Exception {
        UUID accountId = UUID.randomUUID();
        BalanceResponse mockBalance = new BalanceResponse(new BigDecimal("2000.00"), new BigDecimal("1000.00"), new BigDecimal("3000.00"));

        when(getAccountBalancePort.execute(eq(accountId), any(), any())).thenReturn(mockBalance);

        mockMvc.perform(get("/transactions/reports/balance/{accountId}", accountId)
                        .with(authentication(AUTH))
                        .param("month", "5").param("year", "2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(3000.00));
    }

    @Test
    @DisplayName("Should delete transaction successfully")
    void shouldDeleteTransaction() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/transactions/{id}", id)
                        .with(csrf()).with(authentication(AUTH)))
                .andExpect(status().isOk());

        verify(deleteTransactionPort).execute(id);
    }
}