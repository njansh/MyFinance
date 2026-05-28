package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.out.RecurringTemplateRepositoryPort;
import com.nadson.myfinance.domain.entity.RecurringTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListRecurringTemplatesUseCaseTest {

    @Mock
    private RecurringTemplateRepositoryPort repository;

    @InjectMocks
    private ListRecurringTemplatesUseCase useCase;

    @Test
    @DisplayName("Deve listar todos os templates recorrentes de um usuário")
    void shouldListRecurringTemplatesByUserId() {
        // Arrange
        UUID userId = UUID.randomUUID();
        List<RecurringTemplate> expectedTemplates = List.of(mock(RecurringTemplate.class), mock(RecurringTemplate.class));

        when(repository.findAllByUserId(userId)).thenReturn(expectedTemplates);

        // Act
        List<RecurringTemplate> result = useCase.execute(userId);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(expectedTemplates);
        verify(repository, times(1)).findAllByUserId(userId);
    }
}