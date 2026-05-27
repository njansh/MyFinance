package com.nadson.myfinance.infrastructure.adapter.persistence;

import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.domain.enums.AccountType;
import com.nadson.myfinance.domain.enums.TransactionStatus;
import com.nadson.myfinance.domain.enums.TransactionType;
import com.nadson.myfinance.infrastructure.adapter.persistence.entity.AccountJpaEntity;
import com.nadson.myfinance.infrastructure.adapter.persistence.entity.UserJpaEntity;
import com.nadson.myfinance.infrastructure.adapter.persistence.repository.SpringAccountRepository;
import com.nadson.myfinance.infrastructure.adapter.persistence.repository.SpringTransactionRepository;
import com.nadson.myfinance.infrastructure.adapter.persistence.repository.SpringUserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = "spring.flyway.enabled=false")
@Import(TransactionPersistenceAdapter.class)
class TransactionPersistenceAdapterTest {

    @Autowired private TransactionPersistenceAdapter adapter;
    @Autowired private SpringUserRepository userRepository;
    @Autowired private SpringAccountRepository accountRepository;
    @Autowired private SpringTransactionRepository transactionRepository;
    @Autowired private EntityManager entityManager;

    private UUID accountId;

    @BeforeEach
    void setup() {
        // 1. Criar Usuário (FK para Account)
        UUID userId = UUID.randomUUID();
        UserJpaEntity user = new UserJpaEntity();
        user.setId(userId);
        user.setName("Tx User");
        user.setEmail("tx@test.com");
        user.setPassword("pass123");
        userRepository.save(user);

        // 2. Criar Conta (FK para Transaction)
        accountId = UUID.randomUUID();
        AccountJpaEntity account = new AccountJpaEntity(accountId, userId, AccountType.CHECKING, "Main Acc", BigDecimal.ZERO);
        accountRepository.save(account);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("Should save and retrieve transaction")
    void shouldSaveAndRetrieveTransaction() {
        Transaction tx = new Transaction(
                UUID.randomUUID(), "Compra de Teste", new BigDecimal("150.00"),
                LocalDateTime.now(), TransactionType.EXPENSE, accountId,
                null, false, null, null, TransactionStatus.COMPLETED, null
        );

        Transaction savedTx = adapter.save(tx);
        Transaction foundTx = adapter.findById(savedTx.getTransactionId());

        assertThat(foundTx).isNotNull();
        assertThat(foundTx.getDescription()).isEqualTo("Compra de Teste");
        assertThat(foundTx.getAmount()).isEqualByComparingTo("150.00");
    }
}