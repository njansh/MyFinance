package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.out.RecurringTemplateRepositoryPort;
import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
import com.nadson.myfinance.domain.entity.RecurringTemplate;
import com.nadson.myfinance.domain.exception.BusinessRuleException;
import com.nadson.myfinance.domain.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeleteRecurringTemplateUseCaseTest {

    @Mock private RecurringTemplateRepositoryPort repository;
    @Mock private TransactionRepositoryPort transactionRepositoryPort;

    @InjectMocks
    private DeleteRecurringTemplateUseCase useCase;

    @Test
    @DisplayName("Deve deletar template e transações pendentes com sucesso")
    void shouldDeleteTemplateAndPendingTransactions() {
        UUID userId = UUID.randomUUID();
        UUID templateId = UUID.randomUUID();
        RecurringTemplate template = mock(RecurringTemplate.class);

        when(template.getUserId()).thenReturn(userId);
        when(repository.findById(templateId)).thenReturn(template);

        useCase.execute(userId, templateId);

        verify(transactionRepositoryPort).deletePendingByTemplateId(templateId);
        verify(repository).deleteById(templateId);
    }

    @Test
    @DisplayName("Deve falhar se o template não existir")
    void shouldFailWhenTemplateNotFound() {
        UUID templateId = UUID.randomUUID();
        when(repository.findById(templateId)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () ->
                useCase.execute(UUID.randomUUID(), templateId));
    }

    @Test
    @DisplayName("Deve falhar se o usuário não for o dono do template")
    void shouldFailWhenUnauthorized() {
        UUID ownerId = UUID.randomUUID();
        UUID intruderId = UUID.randomUUID();
        UUID templateId = UUID.randomUUID();
        RecurringTemplate template = mock(RecurringTemplate.class);

        when(template.getUserId()).thenReturn(ownerId);
        when(repository.findById(templateId)).thenReturn(template);

        assertThrows(BusinessRuleException.class, () ->
                useCase.execute(intruderId, templateId));
    }
}