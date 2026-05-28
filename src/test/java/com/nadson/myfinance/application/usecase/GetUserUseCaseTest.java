package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.out.UserRepositoryPort;
import com.nadson.myfinance.domain.entity.User;
import com.nadson.myfinance.domain.exception.UserNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetUserUseCaseTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @InjectMocks
    private GetUserUseCase useCase;

    @Test
    @DisplayName("Deve retornar usuário quando encontrado")
    void shouldReturnUserWhenFound() {
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "Nadson", "nadson@email.com", "hashed");

        when(userRepositoryPort.findById(userId)).thenReturn(user);

        User result = useCase.execute(userId);

        assertThat(result).isEqualTo(user);
        verify(userRepositoryPort, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não encontrado")
    void shouldThrowExceptionWhenUserNotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepositoryPort.findById(userId)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> useCase.execute(userId));
    }
}