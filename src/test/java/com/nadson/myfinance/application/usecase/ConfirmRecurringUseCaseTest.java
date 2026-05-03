package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.CreateTransactionPort;
import com.nadson.myfinance.application.port.out.RecurringTemplateRepositoryPort;
import com.nadson.myfinance.domain.entity.RecurringTemplate;
import com.nadson.myfinance.domain.enums.TransactionType;
import com.nadson.myfinance.domain.exception.BusinessRuleException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfirmRecurringUseCaseTest {

    @Mock private RecurringTemplateRepositoryPort repository;
    @Mock private CreateTransactionPort createTransactionPort;

    @InjectMocks private ConfirmRecurringUseCase useCase;

    @Test
    void shouldThrowExceptionWhenUserTriesToConfirmSomeoneElsesTemplate() {
        UUID ownerId = UUID.randomUUID();
        UUID attackerId = UUID.randomUUID();
        UUID templateId = UUID.randomUUID();

        RecurringTemplate template = new RecurringTemplate(
                templateId, ownerId, UUID.randomUUID(), UUID.randomUUID(),
                "Aluguel", new BigDecimal("1500.00"), TransactionType.EXPENSE, 5, true
        );

        when(repository.findById(templateId)).thenReturn(template);

        assertThrows(BusinessRuleException.class, () ->
                useCase.execute(attackerId, templateId, new BigDecimal("1500.00"), LocalDateTime.now())
        );

        verify(createTransactionPort, never()).execute(any());
    }
}