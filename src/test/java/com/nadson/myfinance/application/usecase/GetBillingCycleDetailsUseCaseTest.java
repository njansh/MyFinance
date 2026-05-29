package com.nadson.myfinance.application.usecase;

import java.util.List;
import java.util.UUID;

import com.nadson.myfinance.domain.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nadson.myfinance.application.port.out.BillingCycleRepositoryPort;
import com.nadson.myfinance.application.port.out.CreditCardInstallmentRepositoryPort;
import com.nadson.myfinance.application.port.out.CreditCardPurchaseRepositoryPort;
import com.nadson.myfinance.application.port.out.CreditCardRepositoryPort;
import com.nadson.myfinance.domain.entity.BillingCycle;
import com.nadson.myfinance.domain.entity.CreditCard;
import com.nadson.myfinance.domain.entity.CreditCardInstallment;
import com.nadson.myfinance.domain.entity.CreditCardPurchase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetBillingCycleDetailsUseCaseTest {
    @Mock private BillingCycleRepositoryPort billingCycleRepository;
    @Mock private CreditCardRepositoryPort creditCardRepository;
    @Mock private CreditCardInstallmentRepositoryPort installmentRepository;
    @Mock private CreditCardPurchaseRepositoryPort purchaseRepository;
    @InjectMocks private GetBillingCycleDetailsUseCase useCase;

    @Test
    @DisplayName("Deve retornar detalhes da fatura por ID com itens mapeados")
    void shouldGetDetailsById() {
        UUID userId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();
        UUID cycleId = UUID.randomUUID();
        CreditCard card = mock(CreditCard.class);
        BillingCycle cycle = mock(BillingCycle.class);
        CreditCardInstallment inst = mock(CreditCardInstallment.class);
        CreditCardPurchase purchase = mock(CreditCardPurchase.class);

        when(card.getUserId()).thenReturn(userId);
        when(creditCardRepository.findById(cardId)).thenReturn(card);
        when(cycle.getCreditCardId()).thenReturn(cardId);
        when(billingCycleRepository.findById(cycleId)).thenReturn(cycle);
        when(installmentRepository.findByBillingCycleId(cycleId)).thenReturn(List.of(inst));
        when(purchaseRepository.findById(any())).thenReturn(purchase);

        var result = useCase.execute(userId, cardId, cycleId);

        assertThat(result.items()).hasSize(1);
    }
    @Test
    @DisplayName("Deve falhar se o cartão não for encontrado ou acesso negado")
    void shouldFailWhenCardNotFoundOrUnauthorized() {
        UUID userId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();
        // Simula cartão inexistente
        when(creditCardRepository.findById(cardId)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () ->
                useCase.execute(userId, cardId, UUID.randomUUID()));
    }

    @Test
    @DisplayName("Deve falhar se a fatura não pertencer ao cartão")
    void shouldFailWhenBillingCycleDoesNotBelongToCard() {
        UUID userId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();
        UUID cycleId = UUID.randomUUID();
        CreditCard card = mock(CreditCard.class);
        BillingCycle cycle = mock(BillingCycle.class);

        when(card.getUserId()).thenReturn(userId);
        when(creditCardRepository.findById(cardId)).thenReturn(card);
        // Simula ciclo com ID de cartão diferente
        when(cycle.getCreditCardId()).thenReturn(UUID.randomUUID());
        when(billingCycleRepository.findById(cycleId)).thenReturn(cycle);

        assertThrows(ResourceNotFoundException.class, () ->
                useCase.execute(userId, cardId, cycleId));
    }

    @Test
    @DisplayName("Deve tratar caso em que a compra é nula no mapeamento")
    void shouldHandleNullPurchaseInMapping() {
        UUID userId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();
        UUID cycleId = UUID.randomUUID();
        CreditCard card = mock(CreditCard.class);
        BillingCycle cycle = mock(BillingCycle.class);
        CreditCardInstallment inst = mock(CreditCardInstallment.class);

        when(card.getUserId()).thenReturn(userId);
        when(creditCardRepository.findById(cardId)).thenReturn(card);
        when(cycle.getCreditCardId()).thenReturn(cardId);
        when(billingCycleRepository.findById(cycleId)).thenReturn(cycle);
        when(installmentRepository.findByBillingCycleId(cycleId)).thenReturn(List.of(inst));
        // Simula purchase nulo
        when(purchaseRepository.findById(any())).thenReturn(null);

        var result = useCase.execute(userId, cardId, cycleId);

        assertEquals("Compra Desconhecida", result.items().get(0).description());
    }
}
