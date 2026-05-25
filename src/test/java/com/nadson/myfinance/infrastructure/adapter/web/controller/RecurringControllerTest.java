//package com.nadson.myfinance.infrastructure.adapter.web.controller;
//
//import com.nadson.myfinance.application.port.in.ConfirmRecurringPort;
//import com.nadson.myfinance.application.port.in.ListPendingRecurringPort;
//import com.nadson.myfinance.domain.entity.RecurringTemplate;
//import com.nadson.myfinance.domain.entity.Transaction;
//import com.nadson.myfinance.domain.enums.TransactionType;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.Collections;
//import java.util.List;
//import java.util.UUID;
//
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.when;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//class RecurringControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private ListPendingRecurringPort listPendingRecurringPort;
//
//    @MockBean
//    private ConfirmRecurringPort confirmRecurringPort;
//
//    private final String USER_ID = "550e8400-e29b-41d4-a716-446655440000";
//    private final UUID USER_UUID = UUID.fromString(USER_ID);
//
//    @Test
//    @DisplayName("Should return 200 and list of pending recurring transactions")
//    void shouldListPendingTransactions() throws Exception {
//        // Ajustado para o construtor de 9 parâmetros:
//        // id, userId, accountId, categoryId, description, expectedAmount, type, frequencyDay, active
//        RecurringTemplate template = new RecurringTemplate(
//                UUID.randomUUID(),
//                USER_UUID,
//                UUID.randomUUID(),
//                UUID.randomUUID(),
//                "Assinatura Streaming",
//                new BigDecimal("34.90"),
//                TransactionType.EXPENSE,
//                15,
//                true
//        );
//
//        when(listPendingRecurringPort.execute(USER_UUID)).thenReturn(List.of(template));
//
//        var auth = new UsernamePasswordAuthenticationToken(USER_ID, null, Collections.emptyList());
//
//        mockMvc.perform(get("/recurring/pending")
//                        .with(authentication(auth)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].description").value("Assinatura Streaming"))
//                .andExpect(jsonPath("$.length()").value(1));
//    }
//
//    @Test
//    @DisplayName("Should return 200 when confirming a recurring transaction")
//    void shouldConfirmTransaction() throws Exception {
//        UUID templateId = UUID.randomUUID();
//        BigDecimal amount = new BigDecimal("150.00");
//
//        // Mock da transação confirmada
//        Transaction confirmed = new Transaction(
//                UUID.randomUUID(), "Energia Elétrica", amount, LocalDateTime.now(),
//                TransactionType.EXPENSE, UUID.randomUUID(), null, false, null, null
//        );
//
//        when(confirmRecurringPort.execute(eq(USER_UUID), eq(templateId), any(BigDecimal.class), any(LocalDateTime.class)))
//                .thenReturn(confirmed);
//
//        var auth = new UsernamePasswordAuthenticationToken(USER_ID, null, Collections.emptyList());
//
//        mockMvc.perform(post("/recurring/{templateId}/confirm", templateId)
//                        .with(csrf())
//                        .with(authentication(auth))
//                        .param("actualAmount", "150.00"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.description").value("Energia Elétrica"))
//                .andExpect(jsonPath("$.amount").value(150.00));
//    }
//
//    @Test
//    @DisplayName("Should return 403 when accessing recurring endpoints without authentication")
//    void shouldReturn403WhenUnauthenticated() throws Exception {
//        mockMvc.perform(get("/recurring/pending"))
//                .andExpect(status().isForbidden());
//    }
//}