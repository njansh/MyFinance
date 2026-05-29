package com.nadson.myfinance.infrastructure.adapter.persistence;

import com.nadson.myfinance.domain.entity.BillingCycle;
import com.nadson.myfinance.domain.enums.BillingCycleStatus;
import com.nadson.myfinance.infrastructure.adapter.persistence.entity.BillingCycleJpaEntity;
import com.nadson.myfinance.infrastructure.adapter.persistence.repository.SpringBillingCycleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness; // Importante para Lenient

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) 
class BillingCyclePersistenceAdapterTest {

    @Mock
    private SpringBillingCycleRepository repository;

    @InjectMocks
    private BillingCyclePersistenceAdapter adapter;

    private BillingCycle createCycle(UUID id, BillingCycleStatus status) {
        LocalDate now = LocalDate.now();
        return new BillingCycle(id, UUID.randomUUID(), now.minusDays(10),
                now.plusDays(10), now.plusDays(15),
                new BigDecimal("100.00"), status);
    }

    private BillingCycleJpaEntity mockEntity(BillingCycleStatus status) {
        BillingCycleJpaEntity entity = mock(BillingCycleJpaEntity.class);
        when(entity.getStatus()).thenReturn(status);
        when(entity.getStartDate()).thenReturn(LocalDate.now().minusDays(10));
        when(entity.getClosingDate()).thenReturn(LocalDate.now().plusDays(10));
        when(entity.toDomain()).thenReturn(createCycle(UUID.randomUUID(), status));
        return entity;
    }

    @Test
    @DisplayName("Deve salvar novo billing cycle")
    void shouldSaveNewBillingCycle() {
        UUID id = UUID.randomUUID();
        BillingCycle cycle = createCycle(id, BillingCycleStatus.OPEN);
        when(repository.findById(id)).thenReturn(Optional.empty());
        when(repository.save(any(BillingCycleJpaEntity.class))).thenReturn(new BillingCycleJpaEntity(cycle));

        BillingCycle result = adapter.save(cycle);

        assertThat(result).isNotNull();
        verify(repository).save(any(BillingCycleJpaEntity.class));
    }

    @Test
    @DisplayName("Deve atualizar billing cycle existente")
    void shouldUpdateExistingBillingCycle() {
        UUID id = UUID.randomUUID();
        BillingCycle cycle = createCycle(id, BillingCycleStatus.OPEN);
        BillingCycleJpaEntity existing = new BillingCycleJpaEntity(cycle);
        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(repository.save(any(BillingCycleJpaEntity.class))).thenReturn(existing);

        BillingCycle result = adapter.save(cycle);

        assertThat(result).isNotNull();
        verify(repository).save(any(BillingCycleJpaEntity.class));
    }

    @Test
    @DisplayName("Deve buscar ciclo aberto por cartão e data")
    void shouldFindOpenCycle() {
        UUID cardId = UUID.randomUUID();
        BillingCycleJpaEntity entity = mockEntity(BillingCycleStatus.OPEN);
        when(repository.findByCreditCardIdAndStatus(cardId, BillingCycleStatus.OPEN)).thenReturn(List.of(entity));

        BillingCycle result = adapter.findOpenCycleByCardId(cardId, LocalDate.now());

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Deve retornar null quando não existir ciclo aberto")
    void shouldReturnNullWhenNoOpenCycleExists() {
        UUID cardId = UUID.randomUUID();
        when(repository.findByCreditCardIdAndStatus(cardId, BillingCycleStatus.OPEN)).thenReturn(List.of());

        BillingCycle result = adapter.findOpenCycleByCardId(cardId, LocalDate.now());

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Deve ignorar ciclos fora do intervalo")
    void shouldIgnoreCyclesOutsideDateRange() {
        UUID cardId = UUID.randomUUID();
        BillingCycleJpaEntity entity = mock(BillingCycleJpaEntity.class);
        when(entity.getStartDate()).thenReturn(LocalDate.now().minusDays(30));
        when(entity.getClosingDate()).thenReturn(LocalDate.now().minusDays(20));
        when(repository.findByCreditCardIdAndStatus(cardId, BillingCycleStatus.OPEN)).thenReturn(List.of(entity));

        BillingCycle result = adapter.findOpenCycleByCardId(cardId, LocalDate.now());

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Deve buscar ciclos não pagos")
    void shouldFindUnpaidCycles() {
        UUID cardId = UUID.randomUUID();
        BillingCycleJpaEntity open = mockEntity(BillingCycleStatus.OPEN);
        BillingCycleJpaEntity paid = mockEntity(BillingCycleStatus.PAID);
        when(repository.findByCreditCardId(cardId)).thenReturn(List.of(open, paid));

        List<BillingCycle> result = adapter.findUnpaidCyclesByCardId(cardId);

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando todos os ciclos estiverem pagos")
    void shouldReturnEmptyWhenAllCyclesArePaid() {
        UUID cardId = UUID.randomUUID();
        BillingCycleJpaEntity paid = mockEntity(BillingCycleStatus.PAID);
        when(repository.findByCreditCardId(cardId)).thenReturn(List.of(paid));

        List<BillingCycle> result = adapter.findUnpaidCyclesByCardId(cardId);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Deve buscar billing cycle por ID")
    void shouldFindById() {
        UUID id = UUID.randomUUID();
        BillingCycleJpaEntity entity = mockEntity(BillingCycleStatus.OPEN);
        when(repository.findById(id)).thenReturn(Optional.of(entity));

        BillingCycle result = adapter.findById(id);
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Deve retornar null quando ID não existir")
    void shouldReturnNullWhenIdDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        BillingCycle result = adapter.findById(id);
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Deve buscar billing cycle por cartão, mês e ano")
    void shouldFindByCardMonthYear() {
        UUID cardId = UUID.randomUUID();
        BillingCycleJpaEntity entity = mockEntity(BillingCycleStatus.OPEN);
        when(repository.findByCreditCardIdAndDueDateBetween(eq(cardId), any(), any())).thenReturn(List.of(entity));

        BillingCycle result = adapter.findByCardIdAndMonthYear(cardId, 5, 2026);
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Deve deletar billing cycles por usuário e conta")
    void shouldDeleteAll() {
        UUID id = UUID.randomUUID();
        adapter.deleteAllByUserId(id);
        adapter.deleteAllByAccountId(id);
        verify(repository).deleteAllByUserId(id);
        verify(repository).deleteAllByAccountId(id);
    }
}