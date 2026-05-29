package com.nadson.myfinance.infrastructure.adapter.persistence;

import com.nadson.myfinance.domain.entity.CreditCardPurchase;
import com.nadson.myfinance.infrastructure.adapter.persistence.entity.CreditCardPurchaseJpaEntity;
import com.nadson.myfinance.infrastructure.adapter.persistence.repository.SpringCreditCardPurchaseRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreditCardPurchasePersistenceAdapterTest {

    @Mock
    private SpringCreditCardPurchaseRepository repository;

    @InjectMocks
    private CreditCardPurchasePersistenceAdapter adapter;

    @Test
    @DisplayName("Deve salvar compra de cartão com sucesso")
    void shouldSavePurchase() {
        CreditCardPurchase purchase = new CreditCardPurchase(UUID.randomUUID(), UUID.randomUUID(),UUID.randomUUID() ,
                "Compra", new BigDecimal("100.00"), 1, LocalDate.now()) ;


        when(repository.save(any(CreditCardPurchaseJpaEntity.class)))
                .thenReturn(new CreditCardPurchaseJpaEntity(purchase));

        CreditCardPurchase result = adapter.save(purchase);

        assertThat(result).isNotNull();
        verify(repository).save(any(CreditCardPurchaseJpaEntity.class));
    }

    @Test
    @DisplayName("Deve buscar compra por ID")
    void shouldFindById() {
        UUID id = UUID.randomUUID();
        CreditCardPurchaseJpaEntity entity = mock(CreditCardPurchaseJpaEntity.class);
        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(entity.toDomain()).thenReturn(mock(CreditCardPurchase.class));

        CreditCardPurchase result = adapter.findById(id);

        assertThat(result).isNotNull();
        verify(repository).findById(id);
    }

    @Test
    @DisplayName("Deve buscar compras por ID do cartão")
    void shouldFindByCreditCardId() {
        UUID cardId = UUID.randomUUID();
        CreditCardPurchaseJpaEntity entity = mock(CreditCardPurchaseJpaEntity.class);
        when(repository.findByCreditCardId(cardId)).thenReturn(List.of(entity));
        when(entity.toDomain()).thenReturn(mock(CreditCardPurchase.class));

        List<CreditCardPurchase> result = adapter.findByCreditCardId(cardId);

        assertThat(result).hasSize(1);
        verify(repository).findByCreditCardId(cardId);
    }
}