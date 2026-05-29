package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.out.*;
import com.nadson.myfinance.domain.entity.*;
import com.nadson.myfinance.domain.enums.TransactionStatus;
import com.nadson.myfinance.domain.enums.TransactionType;
import com.nadson.myfinance.domain.exception.BusinessRuleException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListPendingRecurringUseCaseTest {

    @Mock private UserRepositoryPort userRepositoryPort;
    @Mock private RecurringTemplateRepositoryPort recurringTemplateRepositoryPort;
    @Mock private TransactionRepositoryPort transactionRepositoryPort;

    @InjectMocks
    private ListPendingRecurringUseCase useCase;

    @Test
    @DisplayName("Deve falhar se o usuário não for encontrado")
    void shouldFailWhenUserNotFound() {
        when(userRepositoryPort.findById(any())).thenReturn(null);
        assertThrows(BusinessRuleException.class, () -> useCase.execute(UUID.randomUUID(), 5, 2026));
    }

    @Test
    @DisplayName("Deve gerar transações para templates pendentes e retornar lista")
    void shouldGenerateAndListPendingTransactions() {
        UUID userId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        
        RecurringTemplate template = new RecurringTemplate(
                UUID.randomUUID(), userId, categoryId, accountId,
                "Aluguel", new java.math.BigDecimal("1000.00"), 
                TransactionType.EXPENSE, 5, true
        );

        when(userRepositoryPort.findById(userId)).thenReturn(mock(User.class));
        when(recurringTemplateRepositoryPort.findPendingTemplates(userId, 5, 2026))
                .thenReturn(List.of(template));

        List<Transaction> result = useCase.execute(userId, 5, 2026);

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getDescription()).isEqualTo("Aluguel");
        assertThat(result.get(0).getAmount()).isEqualByComparingTo("1000.00");
        verify(transactionRepositoryPort, atLeastOnce()).save(any(Transaction.class));
        verify(recurringTemplateRepositoryPort).save(template);
    }
    @Test
    @DisplayName("Deve gerar transações com ajuste de dia e virada de ano")
    void shouldHandleDateAdjustmentsAndYearChange() {
        UUID userId = UUID.randomUUID();
        RecurringTemplate template = new RecurringTemplate(
                UUID.randomUUID(), userId, UUID.randomUUID(), UUID.randomUUID(),
                "Assinatura", new java.math.BigDecimal("100.00"),
                TransactionType.EXPENSE, 31, true
        );
        template.setLastExecution(12, 2025);

        when(userRepositoryPort.findById(userId)).thenReturn(mock(User.class));
        when(recurringTemplateRepositoryPort.findPendingTemplates(userId, 2, 2026))
                .thenReturn(List.of(template));

        useCase.execute(userId, 2, 2026);

        verify(recurringTemplateRepositoryPort).save(template);
        verify(transactionRepositoryPort, atLeastOnce()).save(any(Transaction.class));
    }
}
