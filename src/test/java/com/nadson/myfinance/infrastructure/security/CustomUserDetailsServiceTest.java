package com.nadson.myfinance.infrastructure.security;

import com.nadson.myfinance.application.port.out.UserRepositoryPort;
import com.nadson.myfinance.domain.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("Deve carregar o usuário pelo email com sucesso")
    void shouldLoadUserByUsernameSuccessfully() {
        String email = "test@email.com";
        User domainUser = new User(UUID.randomUUID(), "Test User", email, "encodedPassword");

        when(userRepositoryPort.findByEmail(email)).thenReturn(Optional.of(domainUser));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
    }

    @Test
    @DisplayName("Deve lançar UsernameNotFoundException quando email não existir")
    void shouldThrowExceptionWhenUserNotFound() {
        String email = "notfound@email.com";

        when(userRepositoryPort.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () ->
                customUserDetailsService.loadUserByUsername(email)
        );
    }
}