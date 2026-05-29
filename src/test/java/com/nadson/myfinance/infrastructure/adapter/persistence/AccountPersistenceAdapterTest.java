package com.nadson.myfinance.infrastructure.adapter.persistence;

import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.enums.AccountType;
import com.nadson.myfinance.infrastructure.adapter.persistence.entity.AccountJpaEntity;
import com.nadson.myfinance.infrastructure.adapter.persistence.repository.SpringAccountRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountPersistenceAdapterTest {

    @Mock
    private SpringAccountRepository repository;

    @InjectMocks
    private AccountPersistenceAdapter adapter;

    @Test
    @DisplayName("Deve salvar ou atualizar conta com sucesso")
    void shouldSaveAccount() {
        UUID accountId = UUID.randomUUID();
        Account account = new Account(accountId, UUID.randomUUID(), AccountType.CHECKING, "INTER", BigDecimal.TEN);

        AccountJpaEntity entity = new AccountJpaEntity();
        entity.setId(accountId);

        when(repository.findById(accountId)).thenReturn(Optional.empty());
        when(repository.save(any(AccountJpaEntity.class))).thenReturn(entity);

        Account saved = adapter.save(account);

        assertThat(saved).isNotNull();
        verify(repository).save(any(AccountJpaEntity.class));
    }

    @Test
    @DisplayName("Deve realizar débito atômico")
    void shouldPerformAtomicDebit() {
        UUID accountId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("50.00");

        adapter.debit(accountId, amount);

        verify(repository).updateBalanceAtomic(accountId, amount.negate());
    }

    @Test
    @DisplayName("Deve buscar conta por ID")
    void shouldFindById() {
        UUID accountId = UUID.randomUUID();
        AccountJpaEntity entity = new AccountJpaEntity();
        entity.setId(accountId);

        when(repository.findById(accountId)).thenReturn(Optional.of(entity));

        Account found = adapter.findById(accountId);

        assertThat(found).isNotNull();
        verify(repository).findById(accountId);
    }
    @Test
    @DisplayName("Deve deletar conta por ID")
    void shouldDeleteById() {
        UUID accountId = UUID.randomUUID();

        adapter.deleteById(accountId);

        verify(repository).deleteById(accountId);
    }

    @Test
    @DisplayName("Deve buscar contas por ID de usuário")
    void shouldFindByUserId() {
        UUID userId = UUID.randomUUID();
        AccountJpaEntity entity = new AccountJpaEntity();
        // Configurar o ID da entidade se necessário para o toDomain

        when(repository.findByUserId(userId)).thenReturn(List.of(entity));

        List<Account> accounts = adapter.findByUserId(userId);

        assertThat(accounts).hasSize(1);
        verify(repository).findByUserId(userId);
    }

    @Test
    @DisplayName("Deve buscar ID de usuário por ID de conta")
    void shouldFindUserIdByAccountId() {
        UUID accountId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        AccountJpaEntity entity = new AccountJpaEntity();
        entity.setUserId(userId);

        when(repository.findById(accountId)).thenReturn(Optional.of(entity));

        UUID foundUserId = adapter.findUserIdByAccountId(accountId);

        assertThat(foundUserId).isEqualTo(userId);
        verify(repository).findById(accountId);
    }

    @Test
    @DisplayName("Deve realizar update de saldo atômico")
    void shouldUpdateBalanceAtomic() {
        UUID accountId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("100.00");

        adapter.updateBalanceAtomic(accountId, amount);

        verify(repository).updateBalanceAtomic(accountId, amount);
    }
}