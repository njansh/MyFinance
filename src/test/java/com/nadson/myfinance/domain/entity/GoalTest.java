package com.nadson.myfinance.domain.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

 class GoalTest {
    @Test
    void shouldAddAmountToGoalCorrectly() {
        Goal goal = new Goal(null, UUID.randomUUID(), "Casa", new BigDecimal("1000.00"), BigDecimal.ZERO);
        goal.addAmount(new BigDecimal("100.00"));
        assertEquals(new BigDecimal("100.00"), goal.getCurrentAmount());
    }
}
