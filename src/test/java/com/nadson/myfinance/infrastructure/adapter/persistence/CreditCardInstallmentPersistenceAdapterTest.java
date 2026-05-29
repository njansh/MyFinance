package com.nadson.myfinance.infrastructure.adapter.persistence;

import com.nadson.myfinance.domain.entity.CreditCardInstallment;
import com.nadson.myfinance.domain.enums.InstallmentStatus;
import com.nadson.myfinance.infrastructure.adapter.persistence.entity.CreditCardInstallmentJpaEntity;
import com.nadson.myfinance.infrastructure.adapter.persistence.repository.SpringCreditCardInstallmentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
 class CreditCardInstallmentPersistenceAdapterTest {

    @Mock
    private SpringCreditCardInstallmentRepository repository;

    @InjectMocks
    private CreditCardInstallmentPersistenceAdapter adapter;

    private CreditCardInstallment createInstallment(UUID id) {
        return new CreditCardInstallment(id, UUID.randomUUID(), UUID.randomUUID(), 1,
                new BigDecimal("50.00"), InstallmentStatus.PENDING);
    }

    @Test
    @DisplayName("Deve salvar novo installment")
    void shouldSaveNewInstallment() {
        UUID id = UUID.randomUUID();
        CreditCardInstallment installment = createInstallment(id);

        when(repository.findById(id)).thenReturn(Optional.empty());
        when(repository.save(any(CreditCardInstallmentJpaEntity.class)))
                .thenReturn(new CreditCardInstallmentJpaEntity(installment));

        CreditCardInstallment result = adapter.save(installment);

        assertThat(result).isNotNull();
        verify(repository).save(any(CreditCardInstallmentJpaEntity.class));
    }

    @Test
    @DisplayName("Deve atualizar installment existente")
    void shouldUpdateExistingInstallment() {
        UUID id = UUID.randomUUID();
        CreditCardInstallment installment = createInstallment(id);
        CreditCardInstallmentJpaEntity existing = new CreditCardInstallmentJpaEntity(installment);

        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(repository.save(any(CreditCardInstallmentJpaEntity.class))).thenReturn(existing);

        adapter.save(installment);

        verify(repository).save(any(CreditCardInstallmentJpaEntity.class));
    }

    @Test
    @DisplayName("Deve buscar installments por Billing Cycle ID")
    void shouldFindByBillingCycleId() {
        UUID cycleId = UUID.randomUUID();
        CreditCardInstallmentJpaEntity entity = mock(CreditCardInstallmentJpaEntity.class);
        when(entity.toDomain()).thenReturn(createInstallment(UUID.randomUUID()));

        when(repository.findByBillingCycleId(cycleId)).thenReturn(List.of(entity));

        List<CreditCardInstallment> result = adapter.findByBillingCycleId(cycleId);

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("Deve buscar installments por Purchase ID")
    void shouldFindByPurchaseId() {
        UUID purchaseId = UUID.randomUUID();
        CreditCardInstallmentJpaEntity entity = mock(CreditCardInstallmentJpaEntity.class);
        when(entity.toDomain()).thenReturn(createInstallment(UUID.randomUUID()));

        when(repository.findByPurchaseId(purchaseId)).thenReturn(List.of(entity));

        List<CreditCardInstallment> result = adapter.findByPurchaseId(purchaseId);

        assertThat(result).hasSize(1);
    }


}
