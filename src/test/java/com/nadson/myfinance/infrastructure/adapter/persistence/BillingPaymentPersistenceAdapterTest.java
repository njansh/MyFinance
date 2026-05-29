package com.nadson.myfinance.infrastructure.adapter.persistence;

import com.nadson.myfinance.domain.entity.BillingPayment;
import com.nadson.myfinance.infrastructure.adapter.persistence.entity.BillingPaymentJpaEntity;
import com.nadson.myfinance.infrastructure.adapter.persistence.repository.SpringBillingPaymentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillingPaymentPersistenceAdapterTest {

    @Mock
    private SpringBillingPaymentRepository repository;

    @InjectMocks
    private BillingPaymentPersistenceAdapter adapter;

    @Test
    @DisplayName("Deve salvar pagamento de fatura")
    void shouldSaveBillingPayment() {
        // Criar um pagamento dummy (assegure-se de que a entidade tenha o construtor ou método estático correto)
        BillingPayment payment = mock(BillingPayment.class);
        BillingPaymentJpaEntity entity = mock(BillingPaymentJpaEntity.class);

        when(repository.save(any(BillingPaymentJpaEntity.class))).thenReturn(entity);
        when(entity.toDomain()).thenReturn(payment);

        BillingPayment result = adapter.save(payment);

        assertThat(result).isNotNull();
        verify(repository).save(any(BillingPaymentJpaEntity.class));
    }

    @Test
    @DisplayName("Deve deletar pagamentos por usuário")
    void shouldDeleteAllByUserId() {
        UUID userId = UUID.randomUUID();
        adapter.deleteAllByUserId(userId);
        verify(repository).deleteAllByUserId(userId);
    }

    @Test
    @DisplayName("Deve deletar pagamentos por conta")
    void shouldDeleteAllByAccountId() {
        UUID accId = UUID.randomUUID();
        adapter.deleteAllByAccountId(accId);
        verify(repository).deleteByAccountId(accId);
    }
}