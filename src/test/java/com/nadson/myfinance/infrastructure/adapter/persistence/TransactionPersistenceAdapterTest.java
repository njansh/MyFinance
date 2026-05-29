package com.nadson.myfinance.infrastructure.adapter.persistence;

import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.domain.enums.TransactionType;
import com.nadson.myfinance.infrastructure.adapter.persistence.entity.TransactionJpaEntity;
import com.nadson.myfinance.infrastructure.adapter.persistence.repository.SpringTransactionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionPersistenceAdapterTest {

    @Mock
    private SpringTransactionRepository repository;

    @InjectMocks
    private TransactionPersistenceAdapter adapter;

    private final UUID id = UUID.randomUUID();
    private final LocalDateTime now = LocalDateTime.now();

    @Test
    @DisplayName("Deve salvar transação (Nova ou Atualização)")
    void shouldSaveTransaction() {
        Transaction transaction = mock(Transaction.class);
        when(transaction.getTransactionId()).thenReturn(id);

        TransactionJpaEntity entity = mock(TransactionJpaEntity.class);
        when(repository.findById(id)).thenReturn(Optional.of(entity)); // Simula atualização
        when(repository.save(any(TransactionJpaEntity.class))).thenReturn(entity);
        when(entity.toDomain()).thenReturn(transaction);

        Transaction saved = adapter.save(transaction);

        assertThat(saved).isNotNull();
        verify(repository).save(any(TransactionJpaEntity.class));

        // Verifica o map de findById
        Transaction found = adapter.findById(id);
        assertThat(found).isNotNull();
    }

    @Test
    @DisplayName("Deve executar deleções e consultas simples")
    void shouldExecuteSimpleDelegationsAndDeletions() {
        adapter.deleteById(id);
        adapter.deleteAllByAccountId(id);
        adapter.deleteTransferCounterpartsByAccountId(id);
        adapter.deletePendingByTemplateId(id);

        verify(repository).deleteById(id);
        verify(repository).deleteAllByAccountId(id);
        verify(repository).deleteTransferCounterpartsByAccountId(id);
        verify(repository).deleteByTemplateIdAndStatus(eq(id), any());

        when(repository.countWithAllFilters(any(), any(), any(), any(), any())).thenReturn(5L);
        assertThat(adapter.count(id, now, BigDecimal.TEN, "Desc", BigDecimal.ZERO)).isEqualTo(5L);

        when(repository.existsTransferCounterpart(any(), any(), any())).thenReturn(true);
        assertThat(adapter.existsTransferCounterpart(id, now, BigDecimal.TEN)).isTrue();

        when(repository.sumTransactionsBeforeDate(any(), any(), any())).thenReturn(BigDecimal.TEN);
        assertThat(adapter.sumBalanceBeforeDate(List.of(id), now, TransactionType.EXPENSE)).isEqualTo(BigDecimal.TEN);
    }

    @Test
    @DisplayName("Deve buscar e mapear consultas de paginação e listas")
    void shouldExecuteQueriesAndMap() {
        TransactionJpaEntity entity = mock(TransactionJpaEntity.class);
        when(entity.toDomain()).thenReturn(mock(Transaction.class));

        Page<TransactionJpaEntity> page = new PageImpl<>(List.of(entity));
        List<TransactionJpaEntity> list = List.of(entity);
        Pageable unpaged = Pageable.unpaged();

        // Paginação
        when(repository.findByAccountId(eq(id), any())).thenReturn(page);
        when(repository.findByAccountIdAndDateBetween(eq(id), any(), any(), any())).thenReturn(page);
        when(repository.findByAccountIdAndDescriptionContainingIgnoreCase(eq(id), anyString(), any())).thenReturn(page);
        when(repository.findByAccountIdAndDateBetweenAndDescriptionContainingIgnoreCase(eq(id), any(), any(), anyString(), any())).thenReturn(page);

        assertThat(adapter.findByAccountId(id, unpaged)).hasSize(1);
        assertThat(adapter.findByAccountIdAndDateBetween(id, now, now, unpaged)).hasSize(1);
        assertThat(adapter.findByAccountIdAndDescription(id, "Desc", unpaged)).hasSize(1);
        assertThat(adapter.findByAccountIdAndDateBetweenAndDescription(id, now, now, "Desc", unpaged)).hasSize(1);

        // Listas
        when(repository.findAllByAccountId(id)).thenReturn(list);
        when(repository.findAllByAccountIdAndDateBetween(eq(id), any(), any())).thenReturn(list);
        when(repository.findPossibleDuplicates(eq(id), any(), any())).thenReturn(list);
        when(repository.findByTransferID(id)).thenReturn(list);
        when(repository.findAllPendingByUserIdAndMonth(eq(id), any(), any())).thenReturn(list);
        when(repository.findByUserIdAndCategoryIdAndMonthAndYear(eq(id), eq(id), anyInt(), anyInt())).thenReturn(list);

        assertThat(adapter.findAllByAccountId(id)).hasSize(1);
        assertThat(adapter.findAllByAccountIdAndDateBetween(id, now, now)).hasSize(1);
        assertThat(adapter.findPossibleDuplicates(id, now, BigDecimal.TEN)).hasSize(1);
        assertThat(adapter.findAllByTransferID(id)).hasSize(1);
        assertThat(adapter.findAllPendingByUserIdUpToDate(id, now, now)).hasSize(1);
        assertThat(adapter.findAllByUserIdAndCategoryIdAndMonthAndYear(id, id, 1, 2026)).hasSize(1);
    }

    @Test
    @DisplayName("Deve atualizar saldo da transação")
    void shouldUpdateBalance() {
        TransactionJpaEntity entity = mock(TransactionJpaEntity.class);
        when(repository.findById(id)).thenReturn(Optional.of(entity));

        adapter.updateBalance(id, BigDecimal.TEN);

        verify(entity).setAccountBalanceAfter(BigDecimal.TEN);
        verify(repository).save(entity);
    }

    @Test
    @DisplayName("Deve calcular agregações e mapear resultados (incluindo valores nulos)")
    void shouldHandleAggregationsAndMapResults() {
        List<UUID> ids = List.of(id);

        // Testando tratativas de nulo e valores preenchidos para somas diretas
        when(repository.sumSavingsByAccountsAndPeriod(any(), any(), any())).thenReturn(null).thenReturn(BigDecimal.TEN);
        when(repository.sumTransactionsByAccountsAndPeriod(any(), any(), any(), any())).thenReturn(null).thenReturn(BigDecimal.TEN);

        assertThat(adapter.sumSavingsByAccountsAndPeriod(ids, now, now)).isEqualTo(BigDecimal.ZERO);
        assertThat(adapter.sumSavingsByAccountsAndPeriod(ids, now, now)).isEqualTo(BigDecimal.TEN);
        assertThat(adapter.sumTransactionsByAccountsAndPeriod(ids, now, now, TransactionType.EXPENSE)).isEqualTo(BigDecimal.ZERO);
        assertThat(adapter.sumTransactionsByAccountsAndPeriod(ids, now, now, TransactionType.EXPENSE)).isEqualTo(BigDecimal.TEN);

        // Testando mapResults com dados preenchidos e nulos
        List<Object> results = new ArrayList<>();
        results.add(new Object[]{"Alimentação", new BigDecimal("150.00")});
        results.add(new Object[]{null, null}); // Cobertura da branch "Sem Categoria" e "BigDecimal.ZERO"

        when(repository.sumAmountByCategoryAndType(eq(id), any())).thenReturn(results);
        when(repository.sumAmountByCategoryAndTypeAndDateBetween(eq(id), any(), any(), any())).thenReturn(results);

        Map<String, BigDecimal> map1 = adapter.getSumByCategoryAndType(id, TransactionType.EXPENSE);
        Map<String, BigDecimal> map2 = adapter.getSumByCategoryAndTypeAndDateBetween(id, TransactionType.EXPENSE, now, now);

        assertThat(map1).containsEntry("Alimentação", new BigDecimal("150.00")).containsEntry("Sem Categoria", BigDecimal.ZERO);
        assertThat(map2).containsEntry("Alimentação", new BigDecimal("150.00")).containsEntry("Sem Categoria", BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Deve encontrar transação sem par (cobertura total de loops e condições)")
    void shouldFindFirstUnmatchedTransaction() {
        UUID destId = UUID.randomUUID();
        UUID tId = UUID.randomUUID();

        TransactionJpaEntity baseEntity = mock(TransactionJpaEntity.class);
        when(baseEntity.getTransferID()).thenReturn(tId);
        when(baseEntity.getTransactionId()).thenReturn(id);
        when(baseEntity.toDomain()).thenReturn(new Transaction());

        // Contraparte 1: ID igual (falha no primeiro if)
        TransactionJpaEntity counterpart1 = mock(TransactionJpaEntity.class);
        when(counterpart1.getTransactionId()).thenReturn(id);

        // Contraparte 2: ID diferente, mas conta diferente (falha no segundo if)
        TransactionJpaEntity counterpart2 = mock(TransactionJpaEntity.class);
        when(counterpart2.getTransactionId()).thenReturn(UUID.randomUUID());
        when(counterpart2.getAccountId()).thenReturn(UUID.randomUUID());

        // Contraparte 3: Match perfeito
        TransactionJpaEntity counterpart3 = mock(TransactionJpaEntity.class);
        when(counterpart3.getTransactionId()).thenReturn(UUID.randomUUID());
        when(counterpart3.getAccountId()).thenReturn(destId);

        // Execução 1: Match perfeito encontrado
        when(repository.findUnmatchedTransactions(eq(id), any(), any(), any())).thenReturn(List.of(baseEntity));
        when(repository.findByTransferID(tId)).thenReturn(List.of(counterpart1, counterpart2, counterpart3));

        Transaction result1 = adapter.findFirstUnmatchedTransaction(id, now, BigDecimal.TEN, TransactionType.EXPENSE, destId);
        assertThat(result1).isNotNull();

        // Execução 2: Nenhum Match encontrado (retorna null)
        when(repository.findByTransferID(tId)).thenReturn(List.of(counterpart1, counterpart2));
        Transaction result2 = adapter.findFirstUnmatchedTransaction(id, now, BigDecimal.TEN, TransactionType.EXPENSE, destId);
        assertThat(result2).isNull();
    }

}