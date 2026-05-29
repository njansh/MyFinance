package com.nadson.myfinance.domain.records;

import com.nadson.myfinance.domain.enums.TransactionStatus;
import com.nadson.myfinance.domain.enums.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TransactionTemplateMetadataTest {

    @Test
    @DisplayName("Deve testar a instanciação e acesso do record TransactionTemplateMetadata")
    void shouldTestMetadata() {
        // Arrange - Preparando todos os 12 dados no formato correto
        UUID transactionId = UUID.randomUUID();
        UUID transferID = UUID.randomUUID();
        String description = "Test Metadata";
        BigDecimal amount = new BigDecimal("100.00");
        BigDecimal accountBalanceAfter = new BigDecimal("500.00");
        LocalDateTime date = LocalDateTime.now();
        TransactionType type = TransactionType.EXPENSE;
        UUID accountId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        boolean isTransfer = true;
        TransactionStatus status = TransactionStatus.COMPLETED;
        UUID templateId = UUID.randomUUID();

        // Act - Construindo o record
        TransactionTemplateMetadata metadata = new TransactionTemplateMetadata(
                transactionId,
                transferID,
                description,
                amount,
                accountBalanceAfter,
                date,
                type,
                accountId,
                categoryId,
                isTransfer,
                status,
                templateId
        );

        // Assert - Validando cada campo para cobrir 100% das instruções geradas
        assertEquals(transactionId, metadata.transactionId());
        assertEquals(transferID, metadata.transferID());
        assertEquals(description, metadata.description());
        assertEquals(amount, metadata.amount());
        assertEquals(accountBalanceAfter, metadata.accountBalanceAfter());
        assertEquals(date, metadata.date());
        assertEquals(type, metadata.type());
        assertEquals(accountId, metadata.accountId());
        assertEquals(categoryId, metadata.categoryId());
        assertTrue(metadata.isTransfer());
        assertEquals(status, metadata.status());
        assertEquals(templateId, metadata.templateId());
    }
}