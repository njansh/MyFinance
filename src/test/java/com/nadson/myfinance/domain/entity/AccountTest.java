package com.nadson.myfinance.domain.entity;

import com.nadson.myfinance.domain.enums.AccountType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AccountTest {
     @Test
     void shouldUpdateBalanceInMemory() {
         Account acc = new Account(UUID.randomUUID(), UUID.randomUUID(), AccountType.CHECKING, "Teste", new BigDecimal("100.00"));
         acc.deposit(new BigDecimal("50.00"));
         assertEquals(new BigDecimal("150.00"), acc.getBalance());
     }
}
