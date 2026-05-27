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
    @DisplayName("Should create a valid transaction")
    void shouldCreateValidTransaction() {
        Transaction tx = new Transaction(
                UUID.randomUUID(), "Salário", new BigDecimal("5000.00"),
                LocalDateTime.now(), TransactionType.INCOME, UUID.randomUUID(),
                null, false, null, null, TransactionStatus.COMPLETED, null
        );

        assertThat(tx.getDescription()).isEqualTo("Salário");
        assertThat(tx.getAmount()).isEqualByComparingTo("5000.00");
        assertThat(tx.getType()).isEqualTo(TransactionType.INCOME);
    }

    @Test
    @DisplayName("Should throw exception for invalid creation parameters")
    void shouldThrowExceptionForInvalidParameters() {
        UUID accId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        // Descrição vazia
        assertThrows(BusinessRuleException.class, () -> new Transaction(
                UUID.randomUUID(), "", new BigDecimal("100.00"), now, TransactionType.EXPENSE, accId, null, false, null, null, null, null));

        // Valor zero ou negativo
        assertThrows(BusinessRuleException.class, () -> new Transaction(
                UUID.randomUUID(), "Teste", BigDecimal.ZERO, now, TransactionType.EXPENSE, accId, null, false, null, null, null, null));

        assertThrows(BusinessRuleException.class, () -> new Transaction(
                UUID.randomUUID(), "Teste", new BigDecimal("-50.00"), now, TransactionType.EXPENSE, accId, null, false, null, null, null, null));

        // Sem conta vinculada
        assertThrows(BusinessRuleException.class, () -> new Transaction(
                UUID.randomUUID(), "Teste", new BigDecimal("100.00"), now, TransactionType.EXPENSE, null, null, false, null, null, null, null));
    }

    @Test
    @DisplayName("Should update details correctly")
    void shouldUpdateDetails() {
        Transaction tx = new Transaction(
                UUID.randomUUID(), "Original", new BigDecimal("100.00"),
                LocalDateTime.now(), TransactionType.EXPENSE, UUID.randomUUID(),
                null, false, null, null, TransactionStatus.COMPLETED, null
        );

        tx.updateDetails("Atualizado", new BigDecimal("200.00"), null, TransactionType.INCOME, null, null);

        assertThat(tx.getDescription()).isEqualTo("Atualizado");
        assertThat(tx.getAmount()).isEqualByComparingTo("200.00");
        assertThat(tx.getType()).isEqualTo(TransactionType.INCOME);
    }
}