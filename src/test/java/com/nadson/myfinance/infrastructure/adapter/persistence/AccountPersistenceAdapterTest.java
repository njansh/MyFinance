package com.nadson.myfinance.infrastructure.adapter.persistence;

import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.enums.AccountType;
import com.nadson.myfinance.infrastructure.adapter.persistence.entity.UserJpaEntity;
import com.nadson.myfinance.infrastructure.adapter.persistence.repository.SpringAccountRepository;
import com.nadson.myfinance.infrastructure.adapter.persistence.repository.SpringUserRepository;
import jakarta.persistence.EntityManager; // Importe o EntityManager
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = "spring.flyway.enabled=false")
@Import(AccountPersistenceAdapter.class)
class AccountPersistenceAdapterTest {

    @Autowired
    private AccountPersistenceAdapter adapter;

    @Autowired
    private SpringUserRepository userRepository;

    @Autowired
    private SpringAccountRepository accountRepository;

    @Autowired
    private EntityManager entityManager; // Injeta o gerenciador de contexto do JPA

    private UUID userId;

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID();
        UserJpaEntity user = new UserJpaEntity();
        user.setId(userId);
        user.setName("Account Owner");
        user.setEmail("owner@test.com");
        user.setPassword("pass123");
        userRepository.save(user);
    }

    @Test
    @DisplayName("Should save and retrieve account")
    void shouldSaveAndRetrieveAccount() {
        Account account = new Account(userId, "Nubank", AccountType.CHECKING);
        account.deposit(new BigDecimal("1500.00"));

        Account saved = adapter.save(account);
        Account found = adapter.findById(saved.getAccountId());

        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Nubank");
        assertThat(found.getBalance()).isEqualByComparingTo("1500.00");
    }

    @Test
    @DisplayName("Should debit account atomically")
    void shouldDebitAccountAtomic() {
        Account account = adapter.save(new Account(UUID.randomUUID(), userId, AccountType.CHECKING, "Inter", new BigDecimal("1000.00")));

        // Força a inserção da conta no banco real antes de rodar o UPDATE
        entityManager.flush();

        // Roda o UPDATE direto no banco de dados (@Modifying)
        adapter.debit(account.getAccountId(), new BigDecimal("200.00"));

        // Limpa a memória do Hibernate para forçar um novo SELECT
        entityManager.clear();

        Account found = adapter.findById(account.getAccountId());
        assertThat(found.getBalance()).isEqualByComparingTo("800.00");
    }

    @Test
    @DisplayName("Should delete account")
    void shouldDeleteAccount() {
        Account account = adapter.save(new Account(userId, "To Delete", AccountType.CHECKING));

        entityManager.flush(); // Força inserção

        adapter.deleteById(account.getAccountId()); // Roda DELETE direto

        entityManager.clear(); // Limpa memória do Hibernate

        assertThat(adapter.findById(account.getAccountId())).isNull();
    }

    @Test
    @DisplayName("Should find accounts by user ID")
    void shouldFindAccountsByUserId() {
        adapter.save(new Account(userId, "Acc 1", AccountType.CHECKING));
        adapter.save(new Account(userId, "Acc 2", AccountType.SAVINGS));

        List<Account> accounts = adapter.findByUserId(userId);
        assertThat(accounts).hasSize(2);
    }
}