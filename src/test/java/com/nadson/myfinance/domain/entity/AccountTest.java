package com.nadson.myfinance.domain.entity;

import com.nadson.myfinance.domain.enums.AccountType;
import com.nadson.myfinance.domain.exception.BusinessRuleException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AccountTest {

    @Test
    @DisplayName("Should initialize balance to zero if not provided")
    void shouldInitializeBalanceToZero() {
        Account account = new Account(UUID.randomUUID(), "Corrente", AccountType.CHECKING);
        assertThat(account.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should deposit amount correctly")
    void shouldDepositAmount() {
        Account account = new Account(UUID.randomUUID(), "Corrente", AccountType.CHECKING);
        account.deposit(new BigDecimal("100.50"));
        assertThat(account.getBalance()).isEqualByComparingTo("100.50");
    }

    @Test
    @DisplayName("Should withdraw amount correctly")
    void shouldWithdrawAmount() {
        Account account = new Account(UUID.randomUUID(), UUID.randomUUID(), AccountType.CHECKING, "Corrente", new BigDecimal("200.00"));
        account.withdraw(new BigDecimal("50.00"));
        assertThat(account.getBalance()).isEqualByComparingTo("150.00");
    }

    @Test
    @DisplayName("Should throw exception on invalid deposit or withdraw")
    void shouldThrowExceptionOnInvalidOperations() {
        Account account = new Account(UUID.randomUUID(), "Corrente", AccountType.CHECKING);

        assertThrows(BusinessRuleException.class, () -> account.deposit(null));
        assertThrows(BusinessRuleException.class, () -> account.deposit(BigDecimal.ZERO));
        assertThrows(BusinessRuleException.class, () -> account.deposit(new BigDecimal("-10")));
        assertThrows(BusinessRuleException.class, () -> account.withdraw(BigDecimal.ZERO));
    }

    @Test
    @DisplayName("Should update account details and validate business rules")
    void shouldUpdateAccount() {
        Account account = new Account(UUID.randomUUID(), "Corrente", AccountType.CHECKING);
        account.update("Nova Conta", new BigDecimal("500.00"), AccountType.INVESTMENT);

        assertThat(account.getName()).isEqualTo("Nova Conta");
        assertThat(account.getBalance()).isEqualByComparingTo("500.00");
        assertThat(account.getType()).isEqualTo(AccountType.INVESTMENT);

        assertThrows(BusinessRuleException.class, () -> account.update(" ", null, null));
        assertThrows(BusinessRuleException.class, () -> account.update(null, new BigDecimal("-1.00"), null));
    }
}