package com.nadson.myfinance.domain.entity;

import com.nadson.myfinance.domain.exception.BusinessRuleException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreditCardTest {

    @Test
    @DisplayName("Deveria criar CreditCard com sucesso e cobrir getters")
    void shouldCreateCreditCardSuccessfully() {
        UUID id = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        BigDecimal limit = new BigDecimal("1000.00");

        CreditCard card = new CreditCard(id, accountId, userId, "Cartão Nubank", limit, 15, 25);

        assertThat(card.getId()).isEqualTo(id);
        assertThat(card.getAccountId()).isEqualTo(accountId);
        assertThat(card.getUserId()).isEqualTo(userId);
        assertThat(card.getName()).isEqualTo("Cartão Nubank");
        assertThat(card.getCreditLimit()).isEqualByComparingTo(limit);
        assertThat(card.getClosingDay()).isEqualTo(15);
        assertThat(card.getDueDay()).isEqualTo(25);
    }

    @Test
    @DisplayName("Deve disparar exceções de validação no construtor")
    void shouldValidateFields() {
        BigDecimal validLimit = new BigDecimal("1000.00");

        // Nome inválido
        assertThrows(BusinessRuleException.class, () -> new CreditCard(null, null, null, null, validLimit, 15, 25));
        assertThrows(BusinessRuleException.class, () -> new CreditCard(null, null, null, "", validLimit, 15, 25));

        // Limite inválido
        assertThrows(BusinessRuleException.class, () -> new CreditCard(null, null, null, "Cartão", BigDecimal.ZERO, 15, 25));

        // Dias de fechamento/vencimento inválidos
        assertThrows(BusinessRuleException.class, () -> new CreditCard(null, null, null, "Cartão", validLimit, 0, 25));
        assertThrows(BusinessRuleException.class, () -> new CreditCard(null, null, null, "Cartão", validLimit, 32, 25));
        assertThrows(BusinessRuleException.class, () -> new CreditCard(null, null, null, "Cartão", validLimit, 15, 0));
        assertThrows(BusinessRuleException.class, () -> new CreditCard(null, null, null, "Cartão", validLimit, 15, 32));
    }


}
