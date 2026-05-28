package com.nadson.myfinance.infrastructure.adapter.web.controller;

import com.nadson.myfinance.application.port.in.*;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.enums.AccountType;
import com.nadson.myfinance.infrastructure.security.WithMockUserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AccountController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com.nadson.myfinance.infrastructure.security.*"))
@WithMockUserId
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private ListTransactionsPort listTransactionsPort;
    @MockBean private GetAccountport getAccountport;
    @MockBean private CreateAccountPort createAccountPort;
    @MockBean private UpdateAccountPort updateAccountPort;
    @MockBean private DeleteAccountPort deleteAccountPort;

    private final String MOCK_USER_ID = "d6e3e5b0-1234-4321-8765-abcdef123456";

    @BeforeEach
    void setupSecurity() {
        // Forçamos o Principal a ser a String que o seu Controller espera
        var auth = new UsernamePasswordAuthenticationToken(
                MOCK_USER_ID,
                null,
                Collections.emptyList()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("Deve buscar conta por ID com sucesso")
    void shouldGetAccountById() throws Exception {
        UUID id = UUID.randomUUID();
        Account account = new Account(id, UUID.fromString(MOCK_USER_ID), AccountType.CHECKING,"Conta Teste",  BigDecimal.ZERO);
        when(getAccountport.execute(id)).thenReturn(account);

        mockMvc.perform(get("/accounts/" + id))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve deletar conta com sucesso")
    void shouldDeleteAccount() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/accounts/" + id).with(csrf()))
                .andExpect(status().isNoContent());
    }
    @Test
    @DisplayName("Deve criar conta com sucesso")
    void shouldCreateAccount() throws Exception {
        UUID userId = UUID.fromString(MOCK_USER_ID);
        Account account = new Account(UUID.randomUUID(), userId, AccountType.CHECKING, "Nubank", BigDecimal.ZERO);
        
        when(createAccountPort.execute(eq(userId), eq("Nubank"), eq(AccountType.CHECKING)))
                .thenReturn(account);

        String json = """
            {"name": "Nubank", "userId": "%s", "type": "CHECKING"}
            """.formatted(MOCK_USER_ID);

        mockMvc.perform(post("/accounts")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Nubank"));
    }
    @Test
    @DisplayName("Deve atualizar conta com sucesso")
    void shouldUpdateAccount() throws Exception {
        UUID accountId = UUID.randomUUID();
        UUID userId = UUID.fromString(MOCK_USER_ID);

        // CORREÇÃO: Passe a String correta "CHECKING" para o mock
        Account account = new Account(accountId, userId, AccountType.CHECKING, "Inter", BigDecimal.valueOf(100));

        when(updateAccountPort.execute(eq(accountId), eq(userId), eq("Inter"), any(), eq("CHECKING")))
                .thenReturn(account);

        String json = """
            {"name": "Inter", "balance": 100, "type": "CHECKING"}
            """;

        mockMvc.perform(put("/accounts/" + accountId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Inter"));
    }
    @Test
    @DisplayName("Deve listar transações de uma conta")
    void shouldListTransactions() throws Exception {
        UUID accountId = UUID.randomUUID();
        when(listTransactionsPort.execute(eq(accountId), any(), any(), any(), any()))
                .thenReturn(org.springframework.data.domain.Page.empty());

        mockMvc.perform(get("/accounts/" + accountId + "/transactions")
                        .param("month", "5")
                        .param("year", "2026"))
                .andExpect(status().isOk());
    }
}