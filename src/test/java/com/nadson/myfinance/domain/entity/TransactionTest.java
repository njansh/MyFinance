package com.nadson.myfinance.domain.entity;

import com.nadson.myfinance.domain.enums.TransactionType;
import com.nadson.myfinance.domain.exception.BusinessRuleException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    @Test
    void shouldThrowExceptionWhenDescriptionIsEmpty() {
        assertThrows(BusinessRuleException.class, () ->
                new Transaction(UUID.randomUUID(), "", new BigDecimal("100.00"),
                        LocalDateTime.now(), TransactionType.INCOME, UUID.randomUUID(), null, false, null, null)
        );
    }

    @Test
    void shouldThrowExceptionWhenAmountIsZeroOrNegative() {
        assertThrows(BusinessRuleException.class, () ->
                new Transaction(UUID.randomUUID(), "Teste", BigDecimal.ZERO,
                        LocalDateTime.now(), TransactionType.INCOME, UUID.randomUUID(), null, false, null, null)
        );
    }

    @Test
    void shouldUpdateDetailsSuccessfully() {
        Transaction tx = new Transaction(UUID.randomUUID(), "Original", new BigDecimal("100.00"),
                LocalDateTime.now(), TransactionType.INCOME, UUID.randomUUID(), null, false, null, null);

        tx.updateDetails("Novo Nome", new BigDecimal("200.00"), null, null, null, null);

        assertEquals("Novo Nome", tx.getDescription());
        assertEquals(new BigDecimal("200.00"), tx.getAmount());
    }
}