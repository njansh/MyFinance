package com.nadson.myfinance.domain.entity;

import com.nadson.myfinance.domain.exception.BusinessRuleException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserTest {

    @Test
    @DisplayName("Should create user successfully")
    void shouldCreateUser() {
        User user = new User(UUID.randomUUID(), "Nadson", "nadson@test.com", "pass123");
        assertThat(user.getName()).isEqualTo("Nadson");
        assertThat(user.getEmail()).isEqualTo("nadson@test.com");
    }

    @Test
    @DisplayName("Should throw exception when name is null or blank")
    void shouldThrowExceptionWhenNameIsInvalid() {
        assertThrows(BusinessRuleException.class, () -> new User(null, null, "test@test.com", "pass"));
        assertThrows(BusinessRuleException.class, () -> new User(null, "  ", "test@test.com", "pass"));
    }

    @Test
    @DisplayName("Should throw exception when email is null or invalid")
    void shouldThrowExceptionWhenEmailIsInvalid() {
        assertThrows(BusinessRuleException.class, () -> new User(null, "Name", null, "pass"));
        assertThrows(BusinessRuleException.class, () -> new User(null, "Name", "invalid-email", "pass"));
    }

    @Test
    @DisplayName("Should throw exception when password is null or blank")
    void shouldThrowExceptionWhenPasswordIsInvalid() {
        assertThrows(BusinessRuleException.class, () -> new User(null, "Name", "test@test.com", null));
        assertThrows(BusinessRuleException.class, () -> new User(null, "Name", "test@test.com", "  "));
    }

    @Test
    @DisplayName("Should update profile and change password")
    void shouldUpdateProfileAndChangePassword() {
        User user = new User(UUID.randomUUID(), "Name", "test@test.com", "oldPass");

        // Testa updateProfile com campos válidos
        user.updateProfile("New Name", "new@test.com");
        assertThat(user.getName()).isEqualTo("New Name");

        // Testa updateProfile com nulos (o método mantém o valor atual)
        user.updateProfile(null, null);
        assertThat(user.getName()).isEqualTo("New Name");

        // Testa validação do updateProfile
        assertThrows(BusinessRuleException.class, () -> user.updateProfile("", "a@a.com"));
        assertThrows(BusinessRuleException.class, () -> user.updateProfile("Name", "invalid"));

        // Testa changePassword
        user.changePassword("newPass", "encodedNewPass");
        assertThat(user.getPassword()).isEqualTo("encodedNewPass");

        assertThrows(BusinessRuleException.class, () -> user.changePassword("", "any"));
    }
    @Test
    @DisplayName("Should cover all getters and setters for 100% coverage")
    void shouldCoverGettersAndSetters() {
        // Arrange
        UUID id = UUID.randomUUID();
        User user = new User(id, "Nadson", "nadson@test.com", "pass123");

        // Act & Assert - Acessando os Getters
        assertThat(user.getId()).isEqualTo(id);
        assertThat(user.getName()).isEqualTo("Nadson");
        assertThat(user.getEmail()).isEqualTo("nadson@test.com");
        assertThat(user.getPassword()).isEqualTo("pass123");

        // Act & Assert - Acessando o Setter
        user.setPassword("newPass");
        assertThat(user.getPassword()).isEqualTo("newPass");
    }
}