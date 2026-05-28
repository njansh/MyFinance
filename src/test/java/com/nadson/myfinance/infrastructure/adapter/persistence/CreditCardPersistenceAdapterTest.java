package com.nadson.myfinance.infrastructure.adapter.persistence;

import com.nadson.myfinance.domain.entity.CreditCard;
import com.nadson.myfinance.infrastructure.adapter.persistence.entity.CreditCardJpaEntity;
import com.nadson.myfinance.infrastructure.adapter.persistence.repository.SpringBillingCycleRepository;
import com.nadson.myfinance.infrastructure.adapter.persistence.repository.SpringBillingPaymentRepository;
import com.nadson.myfinance.infrastructure.adapter.persistence.repository.SpringCreditCardRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreditCardPersistenceAdapterTest {

    @Mock private SpringCreditCardRepository creditCardRepository;
    @Mock private SpringBillingCycleRepository billingCycleRepository;
    @Mock private SpringBillingPaymentRepository billingPaymentRepository;

    @InjectMocks
    private CreditCardPersistenceAdapter adapter;

    @Test
    @DisplayName("Deve salvar cartão")
    void shouldSave() {
        CreditCard card = new CreditCard(UUID.randomUUID(), UUID.randomUUID(),UUID.randomUUID(),"Nubank",new BigDecimal(2000),10,15 );

        when(creditCardRepository.save(any(CreditCardJpaEntity.class)))
                .thenReturn(new CreditCardJpaEntity(card));

        CreditCard result = adapter.save(card);

        assertThat(result).isNotNull();
        verify(creditCardRepository).save(any(CreditCardJpaEntity.class));
    }

    @Test
    @DisplayName("Deve buscar por ID")
    void shouldFindById() {
        UUID id = UUID.randomUUID();
        CreditCardJpaEntity entity = mock(CreditCardJpaEntity.class);
        CreditCard card = mock(CreditCard.class);
        when(entity.toDomain()).thenReturn(card);
        when(creditCardRepository.findById(id)).thenReturn(Optional.of(entity));

        CreditCard result = adapter.findById(id);

        assertThat(result).isNotNull();
        verify(creditCardRepository).findById(id);
    }

    @Test
    @DisplayName("Deve buscar por User ID")
    void shouldFindByUserId() {
        UUID userId = UUID.randomUUID();
        CreditCardJpaEntity entity = mock(CreditCardJpaEntity.class);
        when(entity.toDomain()).thenReturn(mock(CreditCard.class));
        when(creditCardRepository.findByUserId(userId)).thenReturn(List.of(entity));

        List<CreditCard> result = adapter.findByUserId(userId);

        assertThat(result).hasSize(1);
        verify(creditCardRepository).findByUserId(userId);
    }

    @Test
    @DisplayName("Deve deletar por Account ID (Cascata)")
    void shouldDeleteByAccountId() {
        UUID id = UUID.randomUUID();
        adapter.deleteAllByAccountId(id);
        verify(billingPaymentRepository).deleteByAccountId(id);
        verify(billingCycleRepository).deleteAllByAccountId(id);
        verify(creditCardRepository).deleteAllByAccountId(id);
    }

    @Test
    @DisplayName("Deve deletar por User ID (Cascata)")
    void shouldDeleteByUserId() {
        UUID id = UUID.randomUUID();
        adapter.deleteAllByUserId(id);
        verify(billingPaymentRepository).deleteAllByUserId(id);
        verify(billingCycleRepository).deleteAllByUserId(id);
        verify(creditCardRepository).deleteAllByUserId(id);
    }

    @Test
    @DisplayName("Deve deletar por ID (Cascata)")
    void shouldDeleteById() {
        UUID id = UUID.randomUUID();
        adapter.deleteByID(id);
        verify(billingPaymentRepository).deleteAllByCreditCardId(id);
        verify(billingCycleRepository).deleteAllByCreditCardId(id);
        verify(creditCardRepository).deleteById(id);
    }
}