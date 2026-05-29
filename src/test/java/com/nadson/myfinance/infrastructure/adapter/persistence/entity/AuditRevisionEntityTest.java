package com.nadson.myfinance.infrastructure.adapter.persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

 class AuditRevisionEntityTest {

    @Test
    @DisplayName("Deve testar getters e setters da entidade de auditoria")
    void shouldTestGettersAndSetters() {
        AuditRevisionEntity entity = new AuditRevisionEntity();

        entity.setRev(1);
        entity.setRevtstmp(1654000000L);
        entity.setUserId("user-123");

        assertEquals(1, entity.getRev());
        assertEquals(1654000000L, entity.getRevtstmp());
        assertEquals("user-123", entity.getUserId());
    }
}