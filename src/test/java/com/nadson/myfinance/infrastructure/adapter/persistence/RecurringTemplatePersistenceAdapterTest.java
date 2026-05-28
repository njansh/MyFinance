package com.nadson.myfinance.infrastructure.adapter.persistence;

import com.nadson.myfinance.domain.entity.RecurringTemplate;
import com.nadson.myfinance.infrastructure.adapter.persistence.entity.RecurringTemplateJpaEntity;
import com.nadson.myfinance.infrastructure.adapter.persistence.repository.SpringRecurringTemplateRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RecurringTemplatePersistenceAdapterTest {

    @Mock
    private SpringRecurringTemplateRepository repository;

    @InjectMocks
    private RecurringTemplatePersistenceAdapter adapter;

    @Test
    @DisplayName("Deve salvar template recorrente com sucesso")
    void shouldSaveRecurringTemplate() {
        RecurringTemplate template = mock(RecurringTemplate.class);
        RecurringTemplateJpaEntity entity = mock(RecurringTemplateJpaEntity.class);

        when(repository.save(any(RecurringTemplateJpaEntity.class))).thenReturn(entity);
        when(entity.toDomain()).thenReturn(template);

        RecurringTemplate result = adapter.save(template);

        assertThat(result).isNotNull();
        verify(repository).save(any(RecurringTemplateJpaEntity.class));
    }

    @Test
    @DisplayName("Deve buscar por ID")
    void shouldFindById() {
        UUID id = UUID.randomUUID();
        RecurringTemplateJpaEntity entity = mock(RecurringTemplateJpaEntity.class);
        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(entity.toDomain()).thenReturn(mock(RecurringTemplate.class));

        assertThat(adapter.findById(id)).isNotNull();
    }

    @Test
    @DisplayName("Deve buscar templates pendentes")
    void shouldFindPendingTemplates() {
        UUID userId = UUID.randomUUID();
        RecurringTemplateJpaEntity entity = mock(RecurringTemplateJpaEntity.class);
        when(repository.findPendingTemplates(userId, 5, 2026)).thenReturn(List.of(entity));
        when(entity.toDomain()).thenReturn(mock(RecurringTemplate.class));

        List<RecurringTemplate> result = adapter.findPendingTemplates(userId, 5, 2026);
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("Deve buscar templates ativos")
    void shouldFindActiveTemplates() {
        UUID userId = UUID.randomUUID();
        RecurringTemplateJpaEntity entity = mock(RecurringTemplateJpaEntity.class);
        when(repository.findByUserIdAndActiveTrue(userId)).thenReturn(List.of(entity));
        when(entity.toDomain()).thenReturn(mock(RecurringTemplate.class));

        List<RecurringTemplate> result = adapter.findActiveByUserId(userId);
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("Deve realizar deleções com sucesso")
    void shouldDelete() {
        UUID id = UUID.randomUUID();
        adapter.deleteById(id);
        adapter.deleteAllByUserId(id);
        adapter.deleteAllByAccountId(id);

        verify(repository).deleteById(id);
        verify(repository).deleteAllByUserId(id);
        verify(repository).deleteAllByAccountId(id);
    }
    @Test
    @DisplayName("Deve buscar todos os templates por User ID")
    void shouldFindAllByUserId() {
        UUID userId = UUID.randomUUID();
        RecurringTemplateJpaEntity entity = mock(RecurringTemplateJpaEntity.class);
        when(repository.findAllByUserId(userId)).thenReturn(List.of(entity));
        when(entity.toDomain()).thenReturn(mock(RecurringTemplate.class));

        List<RecurringTemplate> result = adapter.findAllByUserId(userId);

        assertThat(result).hasSize(1);
        verify(repository).findAllByUserId(userId);
    }
}