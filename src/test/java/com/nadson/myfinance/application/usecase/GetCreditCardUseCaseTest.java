package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.out.BillingCycleRepositoryPort;
import com.nadson.myfinance.application.port.out.CreditCardRepositoryPort;
import com.nadson.myfinance.domain.entity.BillingCycle;
import com.nadson.myfinance.domain.entity.CreditCard;
import com.nadson.myfinance.domain.exception.CreditCardNotFoundException;
import com.nadson.myfinance.domain.records.CreditCardWithBalanceDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
 class GetCreditCardUseCaseTest {

    @Mock private CreditCardRepositoryPort repositoryPort;
    @Mock private BillingCycleRepositoryPort billingCycleRepository;

    @InjectMocks
    private GetCreditCardUseCase useCase;

    @Test
    @DisplayName("Deve retornar cartão com cálculo de limite disponível")
    void shouldReturnCreditCardWithBalance() {
        UUID userId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();

        CreditCard card = mock(CreditCard.class);
        when(card.getId()).thenReturn(cardId);
        when(card.getUserId()).thenReturn(userId);
        when(card.getCreditLimit()).thenReturn(new BigDecimal("1000.00"));

        BillingCycle cycle = mock(BillingCycle.class);
        when(cycle.getTotalAmount()).thenReturn(new BigDecimal("300.00"));

        when(repositoryPort.findById(cardId)).thenReturn(card);
        when(billingCycleRepository.findUnpaidCyclesByCardId(cardId)).thenReturn(List.of(cycle));

        CreditCardWithBalanceDTO result = useCase.execute(userId, cardId);

        assertThat(result.availableLimit()).isEqualByComparingTo("700.00");
        verify(billingCycleRepository).findUnpaidCyclesByCardId(cardId);
    }

    @Test
    @DisplayName("Deve falhar se o cartão não existir ou acesso for negado")
    void shouldFailWhenCardNotFoundOrUnauthorized() {
        UUID userId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();

        when(repositoryPort.findById(cardId)).thenReturn(null);

        assertThrows(CreditCardNotFoundException.class, () ->
                useCase.execute(userId, cardId));
    }
}