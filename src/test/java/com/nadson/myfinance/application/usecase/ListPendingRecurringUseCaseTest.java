package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.out.RecurringTemplateRepositoryPort;
import com.nadson.myfinance.application.port.out.UserRepositoryPort;
import com.nadson.myfinance.domain.entity.RecurringTemplate;
import com.nadson.myfinance.domain.entity.User;
import com.nadson.myfinance.domain.enums.TransactionType;
import com.nadson.myfinance.domain.exception.BusinessRuleException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListPendingRecurringUseCaseTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private RecurringTemplateRepositoryPort recurringTemplateRepositoryPort;

    @InjectMocks
    private ListPendingRecurringUseCase useCase;

    @Test
    @DisplayName("Deve listar templates pendentes para um usuário existente")
    void shouldListPendingTemplatesSuccessfully() {
        UUID userId = UUID.randomUUID();
        LocalDate now = LocalDate.now();

        RecurringTemplate template = new RecurringTemplate(
                UUID.randomUUID(), userId, UUID.randomUUID(), UUID.randomUUID(),
                "Assinatura Streaming", new BigDecimal("34.90"), TransactionType.EXPENSE, 10,true
        );

        when(userRepositoryPort.findById(userId)).thenReturn(mock(User.class));
        when(recurringTemplateRepositoryPort.findPendingTemplates(
                eq(userId), eq(now.getDayOfMonth()), eq(now.getMonthValue()), eq(now.getYear())))
                .thenReturn(List.of(template));

        List<RecurringTemplate> result = useCase.execute(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Assinatura Streaming", result.get(0).getDescription());
        verify(recurringTemplateRepositoryPort).findPendingTemplates(any(), anyInt(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("Deve lançar BusinessRuleException quando o usuário não for encontrado")
    void shouldThrowExceptionWhenUserNotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepositoryPort.findById(userId)).thenReturn(null);

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () ->
                useCase.execute(userId));

        assertEquals("User not found", exception.getMessage());
        verifyNoInteractions(recurringTemplateRepositoryPort);
    }
}
