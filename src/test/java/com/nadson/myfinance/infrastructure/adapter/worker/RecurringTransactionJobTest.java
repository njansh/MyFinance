package com.nadson.myfinance.infrastructure.adapter.worker;

import com.nadson.myfinance.application.port.in.ConfirmRecurringPort;
import com.nadson.myfinance.infrastructure.adapter.persistence.entity.RecurringTemplateJpaEntity;
import com.nadson.myfinance.infrastructure.adapter.persistence.repository.SpringRecurringTemplateRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecurringTransactionJobTest {

    @Mock
    private SpringRecurringTemplateRepository repository;

    @Mock
    private ConfirmRecurringPort confirmRecurringPort;

    @InjectMocks
    private RecurringTransactionJob recurringTransactionJob;

    @Test
    @DisplayName("Deve processar transações recorrentes pendentes")
    void shouldProcessPendingRecurringTransactions() {
        RecurringTemplateJpaEntity template = new RecurringTemplateJpaEntity();
        template.setId(UUID.randomUUID());
        template.setActive(true);
        template.setFrequencyDay(LocalDate.now().getDayOfMonth());
        // Força o mês anterior para disparar a cobrança
        template.setLastExecutedMonth(LocalDate.now().minusMonths(1).getMonthValue());
        template.setLastExecutedYear(LocalDate.now().getYear());

        when(repository.findAll()).thenReturn(List.of(template));

        recurringTransactionJob.processPendingRecurringTransactions();

        verify(confirmRecurringPort, times(1)).execute(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Não deve estourar erro se ocorrer falha ao processar um template específico (cobertura do catch)")
    void shouldCatchExceptionOnIndividualTemplate() {
        RecurringTemplateJpaEntity template = new RecurringTemplateJpaEntity();
        template.setId(UUID.randomUUID());
        template.setActive(true);
        template.setFrequencyDay(LocalDate.now().getDayOfMonth());

        when(repository.findAll()).thenReturn(List.of(template));
        doThrow(new RuntimeException("Simulated Error")).when(confirmRecurringPort).execute(any(), any(), any(), any());

        // O Job não deve propagar o erro, deve apenas logar e continuar
        recurringTransactionJob.processPendingRecurringTransactions();

        verify(confirmRecurringPort, times(1)).execute(any(), any(), any(), any());
    }
    @Test
    @DisplayName("Não deve processar template se estiver inativo")
    void shouldNotProcessInactiveTemplate() {
        RecurringTemplateJpaEntity template = new RecurringTemplateJpaEntity();
        template.setId(UUID.randomUUID());
        template.setActive(false); // INATIVO
        template.setFrequencyDay(LocalDate.now().getDayOfMonth());

        when(repository.findAll()).thenReturn(List.of(template));

        recurringTransactionJob.processPendingRecurringTransactions();

        // Garante que a porta nunca foi chamada
        verify(confirmRecurringPort, never()).execute(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Deve processar template se o último mês executado for nulo")
    void shouldProcessWhenLastExecutedMonthIsNull() {
        RecurringTemplateJpaEntity template = new RecurringTemplateJpaEntity();
        template.setId(UUID.randomUUID());
        template.setActive(true);
        template.setFrequencyDay(LocalDate.now().getDayOfMonth());
        template.setLastExecutedMonth(null); // NULO
        template.setLastExecutedYear(LocalDate.now().getYear());

        when(repository.findAll()).thenReturn(List.of(template));

        recurringTransactionJob.processPendingRecurringTransactions();

        verify(confirmRecurringPort, times(1)).execute(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Deve processar template se o último ano executado for do passado")
    void shouldProcessWhenLastExecutedYearIsPast() {
        RecurringTemplateJpaEntity template = new RecurringTemplateJpaEntity();
        template.setId(UUID.randomUUID());
        template.setActive(true);
        template.setFrequencyDay(LocalDate.now().getDayOfMonth());
        template.setLastExecutedMonth(12);
        template.setLastExecutedYear(LocalDate.now().getYear() - 1); // ANO PASSADO

        when(repository.findAll()).thenReturn(List.of(template));

        recurringTransactionJob.processPendingRecurringTransactions();

        verify(confirmRecurringPort, times(1)).execute(any(), any(), any(), any());
    }
}