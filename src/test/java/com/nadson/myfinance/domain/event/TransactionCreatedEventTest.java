package com.nadson.myfinance.domain.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransactionCreatedEventTest {

    @Test
    @DisplayName("Deve cobrir a instanciacao e os metodos do record TransactionCreatedEvent")
    void shouldTestEvent() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("150.00");
        int month = 5;
        int year = 2026;

        // Act
        TransactionCreatedEvent event = new TransactionCreatedEvent(userId, categoryId, amount, month, year);

        // Assert
        assertEquals(userId, event.userId());
        assertEquals(categoryId, event.categoryId());
        assertEquals(amount, event.amount());
        assertEquals(month, event.month());
        assertEquals(year, event.year());
    }
}