package com.nadson.myfinance.domain.entity;

import com.nadson.myfinance.domain.enums.TransactionType;
import com.nadson.myfinance.domain.exception.BusinessRuleException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RecurringTemplateTest {

    @Test
    @DisplayName("Deveria criar RecurringTemplate com sucesso e cobrir getters/setters")
    void shouldCreateRecurringTemplateSuccessfully() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID accId = UUID.randomUUID();
        UUID catId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("100.00");

        RecurringTemplate template = new RecurringTemplate(id, userId, accId, catId, "Aluguel", amount, TransactionType.EXPENSE, 5, true);

        // Testa getters
        assertThat(template.getId()).isEqualTo(id);
        assertThat(template.getUserId()).isEqualTo(userId);
        assertThat(template.getAccountId()).isEqualTo(accId);
        assertThat(template.getCategoryId()).isEqualTo(catId);
        assertThat(template.getDescription()).isEqualTo("Aluguel");
        assertThat(template.getExpectedAmount()).isEqualByComparingTo(amount);
        assertThat(template.getAmount()).isEqualByComparingTo(amount);
        assertThat(template.getType()).isEqualTo(TransactionType.EXPENSE);
        assertThat(template.getFrequencyDay()).isEqualTo(5);
        assertThat(template.isActive()).isTrue();

        // Testa setters e lógica de execução
        template.setActive(false);
        assertThat(template.isActive()).isFalse();

        template.setLastExecution(5, 2026);
        assertThat(template.getLastExecutedMonth()).isEqualTo(5);
        assertThat(template.getLastExecutedYear()).isEqualTo(2026);
    }

    @Test
    @DisplayName("Deve disparar exceções de validação no construtor")
    void shouldValidateFields() {
        UUID validId = UUID.randomUUID();
        BigDecimal validAmount = new BigDecimal("100.00");

        // userId nulo
        assertThrows(BusinessRuleException.class, () -> new RecurringTemplate(null, null, validId, null, "Desc", validAmount, TransactionType.EXPENSE, 1, true));
        // accountId nulo
        assertThrows(BusinessRuleException.class, () -> new RecurringTemplate(null, validId, null, null, "Desc", validAmount, TransactionType.EXPENSE, 1, true));
        // description nulo ou vazio
        assertThrows(BusinessRuleException.class, () -> new RecurringTemplate(null, validId, validId, null, "", validAmount, TransactionType.EXPENSE, 1, true));
        // amount zero ou negativo
        assertThrows(BusinessRuleException.class, () -> new RecurringTemplate(null, validId, validId, null, "Desc", BigDecimal.ZERO, TransactionType.EXPENSE, 1, true));
        // type nulo
        assertThrows(BusinessRuleException.class, () -> new RecurringTemplate(null, validId, validId, null, "Desc", validAmount, null, 1, true));
        // frequencyDay fora do intervalo 1-31
        assertThrows(BusinessRuleException.class, () -> new RecurringTemplate(null, validId, validId, null, "Desc", validAmount, TransactionType.EXPENSE, 0, true));
        assertThrows(BusinessRuleException.class, () -> new RecurringTemplate(null, validId, validId, null, "Desc", validAmount, TransactionType.EXPENSE, 32, true));
    }
}