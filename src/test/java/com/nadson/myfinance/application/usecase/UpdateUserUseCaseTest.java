package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.out.UserRepositoryPort;
import com.nadson.myfinance.domain.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateUserUseCaseTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @InjectMocks
    private UpdateUserUseCase useCase;

    @Test
    @DisplayName("Deve lançar RuntimeException quando o utilizador não for encontrado")
    void shouldThrowExceptionWhenUserNotFound() {
        // Arrange
        UUID userId = UUID.randomUUID();
        when(userRepositoryPort.findById(userId)).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                useCase.execute(userId, "Nadson", "nadson@email.com")
        );

        assertThat(exception.getMessage()).isEqualTo("User not found");
        verify(userRepositoryPort, times(1)).findById(userId);
        verify(userRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar e guardar o perfil do utilizador com sucesso")
    void shouldUpdateUserProfileSuccessfully() {
        // Arrange
        UUID userId = UUID.randomUUID();
        User userMock = mock(User.class);

        when(userRepositoryPort.findById(userId)).thenReturn(userMock);
        when(userRepositoryPort.save(userMock)).thenReturn(userMock);

        // Act
        User result = useCase.execute(userId, "Nadson Silva", "nadson.silva@email.com");

        // Assert
        assertThat(result).isNotNull().isEqualTo(userMock);
        verify(userMock, times(1)).updateProfile("Nadson Silva", "nadson.silva@email.com");
        verify(userRepositoryPort, times(1)).findById(userId);
        verify(userRepositoryPort, times(1)).save(userMock);
    }
}