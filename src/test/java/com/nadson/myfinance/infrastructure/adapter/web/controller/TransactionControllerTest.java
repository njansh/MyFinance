package com.nadson.myfinance.infrastructure.adapter.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nadson.myfinance.application.port.in.ConfirmRecurringPort;
import com.nadson.myfinance.application.port.in.CreateTransactionPort;
import com.nadson.myfinance.application.port.in.DeleteTransactionPort;
import com.nadson.myfinance.application.port.in.GetAccountBalancePort;
import com.nadson.myfinance.application.port.in.GetExpensesByCategoryPort;
import com.nadson.myfinance.application.port.in.GetIncomesByCategoryPort;
import com.nadson.myfinance.application.port.in.GetTransactionPort;
import com.nadson.myfinance.application.port.in.TransferPort;
import com.nadson.myfinance.application.port.in.UpdateTransactionPort;
import com.nadson.myfinance.application.usecase.CreateTransactionUseCase;
import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.domain.enums.TransactionStatus;
import com.nadson.myfinance.domain.enums.TransactionType;
import com.nadson.myfinance.infrastructure.adapter.web.dto.request.TransactionRequest;
import com.nadson.myfinance.infrastructure.adapter.web.dto.request.TransferRequest;
import com.nadson.myfinance.infrastructure.adapter.web.dto.response.BalanceResponse;
import com.nadson.myfinance.infrastructure.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.StringRedisTemplate;
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
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
@AutoConfigureMockMvc(addFilters = false)
class TransactionControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private CreateTransactionPort createTransactionPort;
    @MockBean private GetTransactionPort getTransactionPort;
    @MockBean private GetExpensesByCategoryPort getExpensesByCategoryPort;
    @MockBean private UpdateTransactionPort updateTransactionPort;
    @MockBean private DeleteTransactionPort deleteTransactionPort;
    @MockBean private GetAccountBalancePort getAccountBalancePort;
    @MockBean private GetIncomesByCategoryPort getIncomesByCategoryPort;
    @MockBean private TransferPort transferPort;
    @MockBean private ConfirmRecurringPort confirmRecurringPort;
    @MockBean private StringRedisTemplate stringRedisTemplate;
    @MockBean private JwtService jwtService;

    private static final String USER_ID = "550e8400-e29b-41d4-a716-446655440000";
    private static final UsernamePasswordAuthenticationToken AUTH =
            new UsernamePasswordAuthenticationToken(USER_ID, null, Collections.emptyList());
    
    @Nested
    class CreateTransactionTests {

        @Test
        @DisplayName("Should create transaction and return 201")
        void shouldCreateTransaction() throws Exception {

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

            Transaction transaction = new Transaction(
                    UUID.randomUUID(),
                    "Compra Supermercado",
                    new BigDecimal("150.00"),
                    LocalDateTime.now(),
                    TransactionType.EXPENSE,
                    request.accountId(),
                    request.categoryId(),
                    false,
                    null,
                    new BigDecimal("850.00"),
                    TransactionStatus.COMPLETED,
                    null
            );

            when(createTransactionPort.execute(any(Transaction.class)))
                    .thenReturn(new CreateTransactionUseCase.TransactionResult(transaction, null));

            mockMvc.perform(post("/transactions")
                            .with(csrf())
                            .with(authentication(AUTH))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.description")
                            .value("Compra Supermercado"));
        }
    }

    @Nested
    class TransferTests {

        @Test
        @DisplayName("Should execute transfer between accounts")
        void shouldExecuteTransfer() throws Exception {

            UUID originAccountId = UUID.randomUUID();
            UUID destinationAccountId = UUID.randomUUID();

            TransferRequest request = new TransferRequest(
                    originAccountId,
                    destinationAccountId,
                    new BigDecimal("500.00"),
                    LocalDateTime.now(),
                    "Transferência manual"
            );

            mockMvc.perform(post("/transactions/transfer")
                            .with(csrf())
                            .with(authentication(AUTH))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNoContent());

            verify(transferPort).execute(
                    eq(originAccountId),
                    eq(destinationAccountId),
                    eq(new BigDecimal("500.00")),
                    any(LocalDateTime.class),
                    eq("Transferência manual"),
                    isNull(),
                    isNull()
            );
        }
    }

 @Nested
    class UpdateTransactionTests {

        @Test
        @DisplayName("Should update transaction successfully")
        void shouldUpdateTransaction() throws Exception {

            UUID transactionId = UUID.randomUUID();
            UUID accountId = UUID.randomUUID();
            UUID categoryId = UUID.randomUUID();

            Transaction updatedTransaction = new Transaction(
                    transactionId,
                    "Updated transaction",
                    new BigDecimal("200.00"),
                    LocalDateTime.parse("2026-05-03T10:00:00"),
                    TransactionType.EXPENSE,
                    accountId,
                    categoryId,
                    false,
                    null,
                    null,
                    TransactionStatus.COMPLETED,
                    null
            );

            when(updateTransactionPort.execute(
                    eq(transactionId),
                    eq("Updated transaction"),
                    eq(new BigDecimal("200.00")),
                    any(LocalDateTime.class),
                    eq(TransactionType.EXPENSE),
                    eq(accountId),
                    eq(categoryId)
            )).thenReturn(
                    new CreateTransactionUseCase.TransactionResult(
                            updatedTransaction,
                            null
                    )
            );

            String body = """
                {
                    "description": "Updated transaction",
                    "amount": 200.00,
                    "date": "2026-05-03T10:00:00",
                    "type": "EXPENSE",
                    "accountId": "%s",
                    "categoryId": "%s",
                    "goalId": null
                }
                """.formatted(accountId, categoryId);

            mockMvc.perform(put("/transactions/{id}", transactionId)
                            .with(csrf())
                            .with(authentication(AUTH))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.description").value("Updated transaction"));

            verify(updateTransactionPort).execute(
                    eq(transactionId),
                    eq("Updated transaction"),
                    eq(new BigDecimal("200.00")),
                    any(LocalDateTime.class),
                    eq(TransactionType.EXPENSE),
                    eq(accountId),
                    eq(categoryId)
            );
        }
    }

    @Nested
    class GetTransactionTests {

        @Test
        @DisplayName("Should return transaction by ID")
        void shouldReturnTransactionById() throws Exception {

            UUID transactionId = UUID.randomUUID();

            Transaction transaction = new Transaction(
                    transactionId,
                    "Test",
                    BigDecimal.TEN,
                    LocalDateTime.now(),
                    TransactionType.INCOME,
                    UUID.randomUUID(),
                    null,
                    false,
                    null,
                    null,
                    TransactionStatus.COMPLETED,
                    null
            );

            when(getTransactionPort.execute(transactionId))
                    .thenReturn(transaction);

            mockMvc.perform(get("/transactions/{id}", transactionId)
                            .with(authentication(AUTH)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.description").value("Test"));
        }
    }

    @Nested
    class BalanceReportTests {

        @Test
        @DisplayName("Should return balance report")
        void shouldReturnBalanceReport() throws Exception {

            UUID accountId = UUID.randomUUID();

            BalanceResponse response = new BalanceResponse(
                    new BigDecimal("2000.00"),
                    new BigDecimal("1000.00"),
                    new BigDecimal("3000.00")
            );

            when(getAccountBalancePort.execute(
                    eq(accountId),
                    any(LocalDateTime.class),
                    any(LocalDateTime.class)
            )).thenReturn(response);

            mockMvc.perform(get("/transactions/reports/balance/{accountId}", accountId)
                            .with(authentication(AUTH))
                            .param("month", "5")
                            .param("year", "2026"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.balance").value(3000.00));
        }
    }

    @Nested
    class ExpensesReportTests {

        @Test
        @DisplayName("Should return expenses grouped by category")
        void shouldReturnExpensesGroupedByCategory() throws Exception {

            UUID accountId = UUID.randomUUID();

            Map<String, BigDecimal> report =
                    Map.of("Food", new BigDecimal("100.00"));

            when(getExpensesByCategoryPort.execute(
                    eq(accountId),
                    any(LocalDateTime.class),
                    any(LocalDateTime.class)
            )).thenReturn(report);

            mockMvc.perform(get("/transactions/reports/expenses-by-category/{accountId}", accountId)
                            .with(authentication(AUTH))
                            .param("month", "5")
                            .param("year", "2026"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.Food").value(100.00));
        }
    }

    @Nested
    class IncomeReportTests {

        @Test
        @DisplayName("Should return incomes grouped by category")
        void shouldReturnIncomesGroupedByCategory() throws Exception {

            UUID accountId = UUID.randomUUID();

            Map<String, BigDecimal> report =
                    Map.of("Salary", new BigDecimal("5000.00"));

            when(getIncomesByCategoryPort.execute(
                    eq(accountId),
                    any(LocalDateTime.class),
                    any(LocalDateTime.class)
            )).thenReturn(report);

            mockMvc.perform(get("/transactions/reports/incomes-by-category/{accountId}", accountId)
                            .with(authentication(AUTH))
                            .param("month", "5")
                            .param("year", "2026"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.Salary").value(5000.00));
        }
    }

    @Nested
    class DeleteTransactionTests {

        @Test
        @DisplayName("Should delete transaction successfully")
        void shouldDeleteTransaction() throws Exception {

            UUID transactionId = UUID.randomUUID();

            mockMvc.perform(delete("/transactions/{id}", transactionId)
                            .with(csrf())
                            .with(authentication(AUTH)))
                    .andExpect(status().isOk());

            verify(deleteTransactionPort).execute(transactionId);
        }
    }
    @Test
    @DisplayName("Deve confirmar transação recorrente com sucesso")
    void shouldConfirmTransaction() throws Exception {
        UUID transactionId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        Transaction confirmedTransaction = new Transaction(
                transactionId,
                "Conta de Luz",
                new BigDecimal("150.00"),
                LocalDateTime.now(),
                TransactionType.EXPENSE,
                accountId,
                categoryId,
                false,
                null,
                null,
                TransactionStatus.COMPLETED,
                null
        );

        when(confirmRecurringPort.execute(any(UUID.class), eq(transactionId), any(BigDecimal.class), any(LocalDateTime.class)))
                .thenReturn(confirmedTransaction);

        var auth = new UsernamePasswordAuthenticationToken(USER_ID, null, Collections.emptyList());
        org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(auth);

        mockMvc.perform(post("/transactions/{id}/confirm", transactionId)
                        .with(csrf())
                        .with(authentication(auth))
                        .param("actualAmount", "150.00")
                        .param("actualDate", "2026-05-28T10:00:00")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Conta de Luz"));

        org.springframework.security.core.context.SecurityContextHolder.clearContext();
    }
}
