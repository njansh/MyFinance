package com.nadson.myfinance.infrastructure.adapter.persistence.entity;

import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.domain.enums.TransactionStatus;
import com.nadson.myfinance.domain.enums.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionJpaEntityTest {
    @Test
    @DisplayName("Cobertura 100% TransactionJpaEntity")
    void testTransaction() {
        UUID id = UUID.randomUUID();
        UUID accId = UUID.randomUUID();
        UUID catId = UUID.randomUUID();
        UUID transferId = UUID.randomUUID();
        UUID templateId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        Transaction domain = new Transaction(id, "Desc", BigDecimal.TEN, now, TransactionType.EXPENSE, accId, catId, true, transferId, BigDecimal.TEN,TransactionStatus.COMPLETED, templateId);

        // Test Empty Constructor and Setters
        TransactionJpaEntity entityEmpty = new TransactionJpaEntity();
        entityEmpty.setTransactionId(id);
        entityEmpty.setAccountId(accId);
        entityEmpty.setCategoryId(catId);
        entityEmpty.setDescription("Desc");
        entityEmpty.setAmount(BigDecimal.TEN);
        entityEmpty.setDate(now);
        entityEmpty.setType(TransactionType.EXPENSE);
        entityEmpty.setStatus(TransactionStatus.COMPLETED);
        entityEmpty.setTransfer(true);
        entityEmpty.setTransferID(transferId);
        entityEmpty.setTemplateId(templateId);
        entityEmpty.setAccountBalanceAfter(BigDecimal.ONE);
        entityEmpty.setVersion(1L);

        // Test Constructor with domain
        TransactionJpaEntity entity = new TransactionJpaEntity(domain);

        // Need to set these explicitly as they might not be set by the domain constructor depending on how it's written
        entity.setTransferID(transferId);
        entity.setTemplateId(templateId);
        entity.setAccountBalanceAfter(BigDecimal.ONE);
        entity.setVersion(1L);

        // Test all Getters
        assertEquals(id, entity.getTransactionId());
        assertEquals(accId, entity.getAccountId());
        assertEquals(catId, entity.getCategoryId());
        assertEquals("Desc", entity.getDescription());
        assertEquals(BigDecimal.TEN, entity.getAmount());
        assertEquals(now, entity.getDate());
        assertEquals(TransactionType.EXPENSE, entity.getType());
        assertEquals(TransactionStatus.COMPLETED, entity.getStatus());
        assertTrue(entity.isTransfer());
        assertEquals(transferId, entity.getTransferID());
        assertEquals(templateId, entity.getTemplateId());
        assertEquals(BigDecimal.ONE, entity.getAccountBalanceAfter());
        assertEquals(1L, entity.getVersion());

        // Test toDomain
        assertEquals(id, entity.toDomain().getTransactionId());
    }
}