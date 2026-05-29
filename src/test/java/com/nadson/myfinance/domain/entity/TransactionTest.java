package com.nadson.myfinance.domain.entity;

import com.nadson.myfinance.domain.enums.TransactionStatus;
import com.nadson.myfinance.domain.enums.TransactionType;
import com.nadson.myfinance.domain.exception.BusinessRuleException;
import com.nadson.myfinance.domain.exception.InvalidTransactionValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransactionTest {

    @Test
    @DisplayName("Should cover all getters and setters for 100% coverage")
    void shouldCoverGettersAndSetters() {
        UUID id = UUID.randomUUID();
        UUID accId = UUID.randomUUID();
        UUID catId = UUID.randomUUID();
        UUID transferId = UUID.randomUUID();
        UUID templateId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        Transaction tx = new Transaction(id, "Desc", BigDecimal.TEN, now,
                TransactionType.INCOME, accId, catId, false, null, BigDecimal.ZERO, TransactionStatus.PENDING, null);

        // Acessando getters
        assertThat(tx.getTransactionId()).isEqualTo(id);
        assertThat(tx.getDescription()).isEqualTo("Desc");
        assertThat(tx.getAmount()).isEqualByComparingTo(BigDecimal.TEN);
        assertThat(tx.getDate()).isEqualTo(now);
        assertThat(tx.getType()).isEqualTo(TransactionType.INCOME);
        assertThat(tx.getAccountId()).isEqualTo(accId);
        assertThat(tx.getCategoryId()).isEqualTo(catId);
        assertThat(tx.isTransfer()).isFalse();
        assertThat(tx.getTransferID()).isNull();
        assertThat(tx.getAccountBalanceAfter()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(tx.getStatus()).isEqualTo(TransactionStatus.PENDING);
        assertThat(tx.getTemplateId()).isNull();

        // Acessando setters (Para cobrir o que está vermelho no IntelliJ)
        tx.setTransactionId(UUID.randomUUID());
        tx.setDescription("Nova");
        tx.setAmount(new BigDecimal("50.00"));
        tx.setDate(now.plusDays(1));
        tx.setType(TransactionType.EXPENSE);
        tx.setAccountId(UUID.randomUUID());
        tx.setCategoryId(UUID.randomUUID());
        tx.setTransfer(true);
        tx.setTransferID(transferId);
        tx.setAccountBalanceAfter(new BigDecimal("100.00"));
        tx.setStatus(TransactionStatus.COMPLETED);
        tx.setTemplateId(templateId);
        tx.markAsCompleted();
    }

    @Test
    @DisplayName("Should test all validation branches in constructor and updateDetails")
    void shouldTestValidations() {
        UUID accId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        // Testa validação do construtor (erros)
        assertThrows(BusinessRuleException.class, () -> new Transaction(UUID.randomUUID(), "", BigDecimal.TEN, now, TransactionType.INCOME, accId, null, false, null, null, null, null));
        assertThrows(BusinessRuleException.class, () -> new Transaction(UUID.randomUUID(), "Desc", null, now, TransactionType.INCOME, accId, null, false, null, null, null, null));
        assertThrows(BusinessRuleException.class, () -> new Transaction(UUID.randomUUID(), "Desc", BigDecimal.ZERO, now, TransactionType.INCOME, accId, null, false, null, null, null, null));
        assertThrows(BusinessRuleException.class, () -> new Transaction(UUID.randomUUID(), "Desc", BigDecimal.TEN, null, TransactionType.INCOME, accId, null, false, null, null, null, null));
        assertThrows(BusinessRuleException.class, () -> new Transaction(UUID.randomUUID(), "Desc", BigDecimal.TEN, now, null, accId, null, false, null, null, null, null));
        assertThrows(BusinessRuleException.class, () -> new Transaction(UUID.randomUUID(), "Desc", BigDecimal.TEN, now, TransactionType.INCOME, null, null, false, null, null, null, null));

        // Testa updateDetails (cobrir branches)
        Transaction tx = new Transaction(UUID.randomUUID(), "Desc", BigDecimal.TEN, now, TransactionType.INCOME, accId, null, false, null, null, null, null);

        // Update com valores válidos
        tx.updateDetails("Novo", new BigDecimal("20.00"), now, TransactionType.EXPENSE, accId, accId);

        // Update com nulos (os 'if (param != null)' que estavam vermelhos)
        tx.updateDetails(null, null, null, null, null, null);

        // Update com descrição vazia (exceção)
        assertThrows(BusinessRuleException.class, () -> tx.updateDetails("  ", BigDecimal.TEN, now, TransactionType.INCOME, accId, null));

        // Update com valor <= 0 (exceção)
        assertThrows(InvalidTransactionValueException.class, () -> tx.updateDetails("Desc", BigDecimal.ZERO, now, TransactionType.INCOME, accId, null));
    }

    @Test
    @DisplayName("Should cover Transfer Constructor")
    void shouldCoverTransferConstructor() {
        // Date null (cobre o 'date != null ? ...')
        Transaction tx1 = new Transaction(UUID.randomUUID(), BigDecimal.TEN, TransactionType.INCOME, "Trans", UUID.randomUUID(), null, null);
        assertThat(tx1.getDate()).isNotNull();

        // Date not null
        LocalDateTime now = LocalDateTime.now();
        Transaction tx2 = new Transaction(UUID.randomUUID(), BigDecimal.TEN, TransactionType.INCOME, "Trans", UUID.randomUUID(), now, null);
        assertThat(tx2.getDate()).isEqualTo(now);
    }

    @Test
    @DisplayName("Should cover updateCategory")
    void shouldCoverUpdateCategory() {
        Transaction tx = new Transaction(UUID.randomUUID(), "Desc", BigDecimal.TEN, LocalDateTime.now(), TransactionType.INCOME, UUID.randomUUID(), null, false, null, null, null, null);

        UUID newCat = UUID.randomUUID();
        tx.updateCategory(newCat);
        assertThat(tx.getCategoryId()).isEqualTo(newCat);

        assertThrows(BusinessRuleException.class, () -> tx.updateCategory(null));
    }
    @Test
    @DisplayName("Should instantiate empty transaction for JPA/Frameworks")
    void shouldInstantiateEmptyTransaction() {
        Transaction tx = new Transaction();
        assertThat(tx).isNotNull();
    }
}