package com.nadson.myfinance.infrastructure.adapter.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nadson.myfinance.application.port.in.CreateAccountPort;
import com.nadson.myfinance.application.port.in.DeleteAccountPort;
import com.nadson.myfinance.application.port.in.GetAccountport;
import com.nadson.myfinance.application.port.in.ListTransactionsPort;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.enums.AccountType;
import com.nadson.myfinance.infrastructure.adapter.web.dto.request.CreateAccountRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ListTransactionsPort listTransactionsPort;

    @MockBean
    private GetAccountport getAccountport;

    @MockBean
    private CreateAccountPort createAccountPort;

    @MockBean
    private DeleteAccountPort deleteAccountPort;

    @Test
    @DisplayName("Deve retornar 403 quando acessar sem token")
    void shouldReturn403WhenAccessingWithoutToken() throws Exception {
        mockMvc.perform(get("/accounts/" + UUID.randomUUID()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar 200 ao buscar conta por ID")
    void shouldReturn200WhenAccountIsFound() throws Exception {
        UUID id = UUID.randomUUID();
        Account mockAccount = new Account(id, UUID.randomUUID(), AccountType.CHECKING, "Conta Corrente", BigDecimal.ZERO);

        when(getAccountport.execute(id)).thenReturn(mockAccount);

        mockMvc.perform(get("/accounts/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Conta Corrente"));
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar 210 ao criar conta com sucesso")
    void shouldReturn201WhenAccountIsCreated() throws Exception {
        UUID userId = UUID.randomUUID();
        CreateAccountRequest request = new CreateAccountRequest("Poupanca", userId, "SAVINGS");

        Account mockAccount = new Account(UUID.randomUUID(), userId, AccountType.SAVINGS, "Poupanca", BigDecimal.ZERO);

        when(createAccountPort.execute(eq(userId), eq("Poupanca"), eq(AccountType.SAVINGS)))
                .thenReturn(mockAccount);

        mockMvc.perform(post("/accounts")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Poupanca"));
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar 200 ao listar transacoes")
    void shouldReturn200WhenListingTransactions() throws Exception {
        UUID id = UUID.randomUUID();
        Page<com.nadson.myfinance.domain.entity.Transaction> page = new PageImpl<>(List.of());

        when(listTransactionsPort.execute(eq(id), any(), any(), any(), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/accounts/{id}/transactions", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("Deve retornar 204 ao deletar conta")
    void shouldReturn204WhenAccountIsDeleted() throws Exception {
        UUID accountId = UUID.randomUUID();
        String userStr = "550e8400-e29b-41d4-a716-446655440000";
        UUID userUuid = UUID.fromString(userStr);

        var auth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                userStr, null, java.util.Collections.emptyList());

        mockMvc.perform(delete("/accounts/{id}", accountId)
                        .with(csrf())
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication(auth)))
                .andExpect(status().isNoContent());

        verify(deleteAccountPort).execute(eq(accountId), eq(userUuid));
    }
}