package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.out.BillingCycleRepositoryPort;
import com.nadson.myfinance.application.port.out.CreditCardRepositoryPort;
import com.nadson.myfinance.application.port.out.UserRepositoryPort;
import com.nadson.myfinance.domain.entity.BillingCycle;
import com.nadson.myfinance.domain.entity.CreditCard;
import com.nadson.myfinance.domain.entity.User;
import com.nadson.myfinance.domain.exception.BusinessRuleException;
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
class ListCreditCardByUserUseCaseTest {

    @Mock private CreditCardRepositoryPort repositoryPort;
    @Mock private UserRepositoryPort userRepository;
    @Mock private BillingCycleRepositoryPort billingCycleRepository;

    @InjectMocks
    private ListCreditCardByUserUseCase useCase;

    @Test
    @DisplayName("Deve retornar lista de cartões com saldos calculados")
    void shouldListCreditCardsWithBalance() {
        UUID userId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();

        CreditCard card = mock(CreditCard.class);
        when(card.getId()).thenReturn(cardId);
        when(card.getCreditLimit()).thenReturn(new BigDecimal("1000.00"));

        BillingCycle cycle = mock(BillingCycle.class);
        when(cycle.getTotalAmount()).thenReturn(new BigDecimal("200.00"));

        when(userRepository.findById(userId)).thenReturn(mock(User.class));
        when(repositoryPort.findByUserId(userId)).thenReturn(List.of(card));
        when(billingCycleRepository.findUnpaidCyclesByCardId(cardId)).thenReturn(List.of(cycle));

        List<CreditCardWithBalanceDTO> result = useCase.execute(userId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).availableLimit()).isEqualByComparingTo("800.00");
    }

    @Test
    @DisplayName("Deve falhar se o usuário não for encontrado")
    void shouldFailWhenUserNotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(null);

        assertThrows(BusinessRuleException.class, () -> useCase.execute(userId));
    }
}