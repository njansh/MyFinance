package com.nadson.myfinance.domain.entity;

import com.nadson.myfinance.domain.exception.BusinessRuleException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreditCardPurchaseTest {

    private final UUID VALID_ID = UUID.randomUUID();
    private final UUID CARD_ID = UUID.randomUUID();
    private final UUID CAT_ID = UUID.randomUUID();
    private final String DESC = "Compra teste";
    private final BigDecimal AMOUNT = new BigDecimal("100.00");
    private final int INSTALLMENTS = 1;
    private final LocalDate DATE = LocalDate.now();

    @Test
    @DisplayName("Deveria criar purchase com sucesso e cobrir getters")
    void shouldCreatePurchaseSuccessfully() {
        CreditCardPurchase purchase = new CreditCardPurchase(VALID_ID, CARD_ID, CAT_ID, DESC, AMOUNT, INSTALLMENTS, DATE);

        assertThat(purchase.getId()).isEqualTo(VALID_ID);
        assertThat(purchase.getCreditCardId()).isEqualTo(CARD_ID);
        assertThat(purchase.getCategoryId()).isEqualTo(CAT_ID);
        assertThat(purchase.getDescription()).isEqualTo(DESC);
        assertThat(purchase.getTotalAmount()).isEqualByComparingTo(AMOUNT);
        assertThat(purchase.getTotalInstallments()).isEqualTo(INSTALLMENTS);
        assertThat(purchase.getPurchaseDate()).isEqualTo(DATE);
    }

    @Test
    @DisplayName("Deve disparar exceções de validação no construtor")
    void shouldValidateFields() {
        // creditCardId nulo
        assertThrows(BusinessRuleException.class, () -> new CreditCardPurchase(null, null, CAT_ID, DESC, AMOUNT, INSTALLMENTS, DATE));
        // categoryId nulo
        assertThrows(BusinessRuleException.class, () -> new CreditCardPurchase(null, CARD_ID, null, DESC, AMOUNT, INSTALLMENTS, DATE));
        // descricao nula ou vazia
        assertThrows(BusinessRuleException.class, () -> new CreditCardPurchase(null, CARD_ID, CAT_ID, "", AMOUNT, INSTALLMENTS, DATE));
        // amount zero ou negativo
        assertThrows(BusinessRuleException.class, () -> new CreditCardPurchase(null, CARD_ID, CAT_ID, DESC, BigDecimal.ZERO, INSTALLMENTS, DATE));
        // parcelas < 1
        assertThrows(BusinessRuleException.class, () -> new CreditCardPurchase(null, CARD_ID, CAT_ID, DESC, AMOUNT, 0, DATE));
        // data nula
        assertThrows(BusinessRuleException.class, () -> new CreditCardPurchase(null, CARD_ID, CAT_ID, DESC, AMOUNT, INSTALLMENTS, null));
    }
}