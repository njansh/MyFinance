package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.CreateCategoryPort;
import com.nadson.myfinance.application.port.out.PasswordEncoderPort;
import com.nadson.myfinance.application.port.out.UserRepositoryPort;
import com.nadson.myfinance.domain.entity.User;
import com.nadson.myfinance.domain.enums.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateUserUseCaseTest {

    @Mock private UserRepositoryPort userRepository;
    @Mock private CreateCategoryPort categoryPort;
    @Mock private PasswordEncoderPort passwordEncoder;

    @InjectMocks
    private CreateUserUseCase useCase;

    @Test
    @DisplayName("Deve criar usuário, criptografar senha e criar 11 categorias padrão")
    void shouldCreateUserAndDefaultCategories() {
        // Arrange
        UUID userId = UUID.randomUUID();
        User savedUser = new User(userId, "Nadson", "nadson@email.com", "hashed_password");

        when(passwordEncoder.encode("123456")).thenReturn("hashed_password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        User result = useCase.execute("Nadson", "nadson@email.com", "123456");

        // Assert
        assertThat(result.getId()).isEqualTo(userId);
        verify(passwordEncoder).encode("123456");
        verify(userRepository).save(any(User.class));

        // Verifica se todas as 11 categorias padrão foram criadas
        verify(categoryPort, times(11)).execute(eq(userId), anyString(), anyString(), anyString(), any(TransactionType.class));
    }
}