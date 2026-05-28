package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.out.BillingCycleRepositoryPort;
import com.nadson.myfinance.application.port.out.CreditCardInstallmentRepositoryPort;
import com.nadson.myfinance.application.port.out.CreditCardPurchaseRepositoryPort;
import com.nadson.myfinance.application.port.out.CreditCardRepositoryPort;
import com.nadson.myfinance.domain.entity.BillingCycle;
import com.nadson.myfinance.domain.entity.CreditCard;
import com.nadson.myfinance.domain.entity.CreditCardInstallment;
import com.nadson.myfinance.domain.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetBillingCycleByDateUseCaseTest {
    @Mock private BillingCycleRepositoryPort billingCycleRepository;
    @Mock private CreditCardRepositoryPort creditCardRepository;
    @Mock private CreditCardInstallmentRepositoryPort installmentRepository;
    @Mock private CreditCardPurchaseRepositoryPort purchaseRepository;
    @InjectMocks
    private GetBillingCycleByDateUseCase useCase;

    @Test
    @DisplayName("Deve retornar detalhes da fatura por data")
    void shouldGetDetailsByDate() {
        UUID userId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();
        CreditCard card = mock(CreditCard.class);
        BillingCycle cycle = mock(BillingCycle.class);

        when(card.getUserId()).thenReturn(userId);
        when(creditCardRepository.findById(cardId)).thenReturn(card);
        when(billingCycleRepository.findByCardIdAndMonthYear(cardId, 5, 2026)).thenReturn(cycle);
        when(installmentRepository.findByBillingCycleId(any())).thenReturn(List.of());

        useCase.execute(userId, cardId, 5, 2026);
        verify(billingCycleRepository).findByCardIdAndMonthYear(cardId, 5, 2026);
    }
    @Test
    @DisplayName("Deve falhar se o cartão não existir ou acesso negado")
    void shouldFailWhenCardNotFoundOrUnauthorized() {
        UUID userId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();

        // Simula cartão inexistente
        when(creditCardRepository.findById(cardId)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () ->
                useCase.execute(userId, cardId, 5, 2026));
    }

    @Test
    @DisplayName("Deve falhar se o ciclo de fatura não existir")
    void shouldFailWhenCycleNotFound() {
        UUID userId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();
        CreditCard card = mock(CreditCard.class);

        when(card.getUserId()).thenReturn(userId);
        when(creditCardRepository.findById(cardId)).thenReturn(card);
        // Simula fatura inexistente
        when(billingCycleRepository.findByCardIdAndMonthYear(cardId, 5, 2026)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () ->
                useCase.execute(userId, cardId, 5, 2026));
    }

    @Test
    @DisplayName("Deve tratar caso em que a compra é nula no mapeamento")
    void shouldHandleNullPurchaseInInstallmentMapping() {
        UUID userId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();
        CreditCard card = mock(CreditCard.class);
        BillingCycle cycle = mock(BillingCycle.class);
        CreditCardInstallment inst = mock(CreditCardInstallment.class);

        when(card.getUserId()).thenReturn(userId);
        when(creditCardRepository.findById(cardId)).thenReturn(card);
        when(billingCycleRepository.findByCardIdAndMonthYear(cardId, 5, 2026)).thenReturn(cycle);
        when(installmentRepository.findByBillingCycleId(any())).thenReturn(List.of(inst));
        // Simula purchase nulo para o branch do ternary operator
        when(purchaseRepository.findById(any())).thenReturn(null);

        var result = useCase.execute(userId, cardId, 5, 2026);

        assertEquals("Unknown Purchase", result.items().get(0).description());
    }
}