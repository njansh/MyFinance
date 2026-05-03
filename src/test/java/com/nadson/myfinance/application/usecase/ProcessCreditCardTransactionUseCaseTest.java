package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.out.BillingCycleRepositoryPort;
import com.nadson.myfinance.application.port.out.CreditCardRepositoryPort;
import com.nadson.myfinance.domain.entity.BillingCycle;
import com.nadson.myfinance.domain.entity.CreditCard;
import com.nadson.myfinance.domain.enums.BillingCycleStatus;
import com.nadson.myfinance.domain.exception.BusinessRuleException;
import com.nadson.myfinance.domain.exception.ResourceNotFoundException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessCreditCardTransactionUseCaseTest {

    @Mock private CreditCardRepositoryPort creditCardRepository;
    @Mock private BillingCycleRepositoryPort billingCycleRepository;

    @InjectMocks
    private ProcessCreditCardTransactionUseCase useCase;

    @Test
    @DisplayName("Deve processar compra parcelada distribuindo valores e ajustando resíduo na última parcela")
    void shouldProcessInstallmentsCorrectly() {
        UUID cardId = UUID.randomUUID();
        LocalDate date = LocalDate.of(2026, 5, 10);

        CreditCard card = new CreditCard(cardId, UUID.randomUUID(), "Mastercard", new BigDecimal("1000.00"), 5, 15);

        BigDecimal totalAmount = new BigDecimal("100.00");
        int installments = 3;

        when(creditCardRepository.findById(cardId)).thenReturn(card);
        when(billingCycleRepository.findUnpaidCyclesByCardId(cardId)).thenReturn(List.of());

        when(billingCycleRepository.findOpenCycleByCardId(eq(cardId), any(LocalDate.class)))
                .thenAnswer(invocation -> new BillingCycle(UUID.randomUUID(), cardId, date, date, date, BigDecimal.ZERO, BillingCycleStatus.OPEN));

        useCase.execute(cardId, totalAmount, date, installments);

        verify(billingCycleRepository, times(3)).save(any(BillingCycle.class));

        verify(billingCycleRepository, times(2)).save(argThat(c -> c.getTotalAmount().compareTo(new BigDecimal("33.33")) == 0));
        verify(billingCycleRepository).save(argThat(c -> c.getTotalAmount().compareTo(new BigDecimal("33.34")) == 0));

        verify(billingCycleRepository).findOpenCycleByCardId(eq(cardId), eq(date.plusMonths(2)));
    }

    @Test
    @DisplayName("Deve lançar exceção quando o limite do cartão é insuficiente")
    void shouldThrowExceptionWhenLimitIsInsufficient() {
        UUID cardId = UUID.randomUUID();
        CreditCard card = new CreditCard(cardId, UUID.randomUUID(), "Visa", new BigDecimal("500.00"), 5, 15);

        LocalDate start = LocalDate.now().minusDays(20);
        LocalDate closing = LocalDate.now().plusDays(5);
        LocalDate due = LocalDate.now().plusDays(15);

        BillingCycle existingCycle = new BillingCycle(
                UUID.randomUUID(),
                cardId,
                start,
                closing,
                due,
                new BigDecimal("450.00"),
                BillingCycleStatus.OPEN
        );

        when(creditCardRepository.findById(cardId)).thenReturn(card);
        when(billingCycleRepository.findUnpaidCyclesByCardId(cardId)).thenReturn(List.of(existingCycle));

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () ->
                useCase.execute(cardId, new BigDecimal("100.00"), LocalDate.now(), 1));

        assertEquals("Limite insuficiente", exception.getMessage());
        verify(billingCycleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando o cartão não for encontrado")
    void shouldThrowExceptionWhenCardNotFound() {
        UUID cardId = UUID.randomUUID();
        when(creditCardRepository.findById(cardId)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () ->
                useCase.execute(cardId, BigDecimal.TEN, LocalDate.now(), 1));

        verifyNoInteractions(billingCycleRepository);
    }

    @Test
    @DisplayName("Deve criar novo ciclo se não houver fatura aberta para o mês da parcela")
    void shouldCreateNewCycleIfNotFound() {
        UUID cardId = UUID.randomUUID();
        CreditCard card = new CreditCard(cardId, UUID.randomUUID(), "Visa", new BigDecimal("1000.00"), 5, 15);

        when(creditCardRepository.findById(cardId)).thenReturn(card);
        when(billingCycleRepository.findUnpaidCyclesByCardId(cardId)).thenReturn(List.of());
        when(billingCycleRepository.findOpenCycleByCardId(any(), any())).thenReturn(null);

        useCase.execute(cardId, new BigDecimal("50.00"), LocalDate.of(2026, 5, 10), 1);

        verify(billingCycleRepository).save(argThat(cycle ->
                cycle.getCreditCardId().equals(cardId) &&
                        cycle.getStatus() == BillingCycleStatus.OPEN
        ));
    }
}