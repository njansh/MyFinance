package com.nadson.myfinance.infrastructure.adapter.persistence;

import com.nadson.myfinance.domain.entity.Budget;
import com.nadson.myfinance.infrastructure.adapter.persistence.entity.BudgetJpaEntity;
import com.nadson.myfinance.infrastructure.adapter.persistence.repository.SpringBudgetRepository;
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
class BudgetPersistenceAdapterTest {

    @Mock
    private SpringBudgetRepository repository;

    @InjectMocks
    private BudgetPersistenceAdapter adapter;



    @Test
    @DisplayName("Deve buscar orçamento por ID")
    void shouldFindById() {
        UUID id = UUID.randomUUID();
        BudgetJpaEntity entity = mock(BudgetJpaEntity.class);
        when(entity.toDomain()).thenReturn(mock(Budget.class));
        when(repository.findById(id)).thenReturn(Optional.of(entity));

        assertThat(adapter.findById(id)).isPresent();
    }

    @Test
    @DisplayName("Deve buscar orçamento por usuário, categoria, mês e ano")
    void shouldFindByParams() {
        UUID userId = UUID.randomUUID();
        UUID catId = UUID.randomUUID();
        BudgetJpaEntity entity = mock(BudgetJpaEntity.class);
        when(entity.toDomain()).thenReturn(mock(Budget.class));

        when(repository.findByUserIdAndCategoryIdAndMonthAndYear(userId, catId, 5, 2026))
                .thenReturn(Optional.of(entity));

        assertThat(adapter.findByUserIdAndCategoryIdAndMonthAndYear(userId, catId, 5, 2026)).isNotNull();
    }

    @Test
    @DisplayName("Deve buscar lista de orçamentos por usuário")
    void shouldFindByUserId() {
        UUID userId = UUID.randomUUID();
        BudgetJpaEntity entity = mock(BudgetJpaEntity.class);
        when(entity.toDomain()).thenReturn(mock(Budget.class));

        when(repository.findByUserId(userId)).thenReturn(List.of(entity));

        assertThat(adapter.findByUserId(userId)).hasSize(1);
    }

    @Test
    @DisplayName("Deve deletar orçamentos por ID e por usuário")
    void shouldDeleteBudgets() {
        UUID id = UUID.randomUUID();
        adapter.deleteById(id);
        adapter.deleteAllByUserId(id);

        verify(repository).deleteById(id);
        verify(repository).deleteAllByUserId(id);
    }
    @Test
    @DisplayName("Deve buscar lista de orçamentos por usuário, mês e ano")
    void shouldFindByUserIdAndMonthAndYear() {
        UUID userId = UUID.randomUUID();
        BudgetJpaEntity entity = mock(BudgetJpaEntity.class);
        when(entity.toDomain()).thenReturn(mock(Budget.class));

        when(repository.findByUserIdAndMonthAndYear(userId, 5, 2026))
                .thenReturn(List.of(entity));

        List<Budget> result = adapter.findByUserIdAndMonthAndYear(userId, 5, 2026);

        assertThat(result).hasSize(1);
        verify(repository).findByUserIdAndMonthAndYear(userId, 5, 2026);
    }
    @Test
    @DisplayName("Deve salvar orçamento com sucesso e garantir cobertura do save")
    void shouldSaveBudget() {
        // 1. Arrange: Crie um Budget real para garantir que o construtor da entidade funcione
        Budget budget = new Budget(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 5, 2026,
                new BigDecimal("100.00"));

        // 2. Mockar a entidade de JPA resultante
        BudgetJpaEntity entity = mock(BudgetJpaEntity.class);
        when(entity.toDomain()).thenReturn(budget); // Garante que o .toDomain() seja chamado

        // 3. Configurar o repositório para retornar a entidade mockada
        when(repository.save(any(BudgetJpaEntity.class))).thenReturn(entity);

        // 4. Act
        Budget result = adapter.save(budget);

        // 5. Assert
        assertThat(result).isNotNull();
        verify(repository).save(any(BudgetJpaEntity.class));
    }
}