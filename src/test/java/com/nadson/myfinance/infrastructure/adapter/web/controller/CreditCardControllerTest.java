package com.nadson.myfinance.infrastructure.adapter.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nadson.myfinance.application.port.in.CreateCreditCardPort;
import com.nadson.myfinance.application.port.in.ProcessCreditCardTransactionPort;
import com.nadson.myfinance.infrastructure.adapter.web.dto.request.CreditCardRequest;
import com.nadson.myfinance.infrastructure.adapter.web.dto.request.CreditCardTransactionRequest;
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
import java.time.LocalDate;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CreditCardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CreateCreditCardPort createCreditCardPort;

    @MockBean
    private ProcessCreditCardTransactionPort processTransactionPort;

    private final String USER_ID = "550e8400-e29b-41d4-a716-446655440000";

    @Test
    @DisplayName("Should create credit card and return 201")
    void shouldCreateCreditCard() throws Exception {
        UUID accountId = UUID.randomUUID();
        CreditCardRequest request = new CreditCardRequest("Nubank", new BigDecimal("5000.00"), 5, 15, accountId);

        var auth = new UsernamePasswordAuthenticationToken(USER_ID, null, Collections.emptyList());

        mockMvc.perform(post("/credit-cards")
                        .with(csrf())
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(createCreditCardPort).execute(eq("Nubank"), any(BigDecimal.class), eq(5), eq(15), eq(accountId));
    }

    @Test
    @DisplayName("Should add transaction to credit card and return 200")
    void shouldAddTransaction() throws Exception {
        UUID cardId = UUID.randomUUID();
        LocalDate now = LocalDate.now();
        CreditCardTransactionRequest request = new CreditCardTransactionRequest(new BigDecimal("150.50"), now, 1);

        var auth = new UsernamePasswordAuthenticationToken(USER_ID, null, Collections.emptyList());

        mockMvc.perform(post("/credit-cards/{id}/transactions", cardId)
                        .with(csrf())
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(processTransactionPort).execute(eq(cardId), any(BigDecimal.class), any(LocalDate.class), eq(1));
    }

    @Test
    @DisplayName("Should return 403 when creating credit card without authentication")
    void shouldReturn403WhenUnauthenticated() throws Exception {
        mockMvc.perform(post("/credit-cards")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }
}