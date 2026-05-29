package com.nadson.myfinance.infrastructure.adapter.persistence;

import com.nadson.myfinance.domain.entity.User;
import com.nadson.myfinance.infrastructure.adapter.persistence.entity.UserJpaEntity;
import com.nadson.myfinance.infrastructure.adapter.persistence.repository.SpringUserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserPersistenceTest {

    @Mock
    private SpringUserRepository repository;

    @InjectMocks
    private UserPersistence adapter;

    @Test
    @DisplayName("Deve salvar usuário com sucesso")
    void shouldSaveUser() {
        User user = mock(User.class);
        UserJpaEntity entity = mock(UserJpaEntity.class);

        when(repository.save(any(UserJpaEntity.class))).thenReturn(entity);
        when(entity.toDomain()).thenReturn(user);

        User result = adapter.save(user);

        assertThat(result).isNotNull();
        verify(repository).save(any(UserJpaEntity.class));
    }

    @Test
    @DisplayName("Deve buscar usuário por ID")
    void shouldFindById() {
        UUID id = UUID.randomUUID();
        UserJpaEntity entity = mock(UserJpaEntity.class);
        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(entity.toDomain()).thenReturn(mock(User.class));

        assertThat(adapter.findById(id)).isNotNull();
    }

    @Test
    @DisplayName("Deve buscar usuário por E-mail")
    void shouldFindByEmail() {
        String email = "teste@exemplo.com";
        UserJpaEntity entity = mock(UserJpaEntity.class);
        when(repository.findByEmail(email)).thenReturn(Optional.of(entity));
        when(entity.toDomain()).thenReturn(mock(User.class));

        assertThat(adapter.findByEmail(email)).isPresent();
    }

    @Test
    @DisplayName("Deve deletar usuário por ID")
    void shouldDeleteById() {
        UUID id = UUID.randomUUID();
        adapter.deleteById(id);
        verify(repository).deleteById(id);
    }
}