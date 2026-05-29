package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.out.UserRepositoryPort;
import com.nadson.myfinance.domain.entity.User;
import com.nadson.myfinance.domain.exception.BusinessRuleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ChangePasswordUseCaseTest {

    private UserRepositoryPort userRepository;
    private PasswordEncoder passwordEncoder;
    private ChangePasswordUseCase useCase;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepositoryPort.class);
        passwordEncoder = mock(PasswordEncoder.class);
        useCase = new ChangePasswordUseCase(userRepository, passwordEncoder);

        // Mock do SecurityContext para injetar o userId
        UUID userId = UUID.randomUUID();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userId.toString(), null)
        );
    }

    @Test
    @DisplayName("Deveria alterar senha com sucesso")
    void shouldChangePasswordSuccessfully() {
        UUID userId = UUID.fromString((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        User user = mock(User.class);

        when(userRepository.findById(userId)).thenReturn(user);
        when(passwordEncoder.matches("oldPass", null)).thenReturn(true); // Ajuste conforme seu User mock
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNew");

        useCase.execute("oldPass", "newPass");

        verify(user).changePassword(anyString(), eq("encodedNew"));
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Deve falhar quando a senha antiga está incorreta")
    void shouldFailWhenOldPasswordIsWrong() {
        UUID userId = UUID.fromString((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        User user = mock(User.class);
        when(user.getPassword()).thenReturn("encodedOld");
        when(userRepository.findById(userId)).thenReturn(user);
        when(passwordEncoder.matches("wrongPass", "encodedOld")).thenReturn(false);

        assertThrows(BusinessRuleException.class, () -> useCase.execute("wrongPass", "newPass"));
    }
}