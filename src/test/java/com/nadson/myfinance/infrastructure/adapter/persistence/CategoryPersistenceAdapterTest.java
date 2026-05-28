package com.nadson.myfinance.infrastructure.adapter.persistence;

import com.nadson.myfinance.domain.entity.Category;
import com.nadson.myfinance.domain.enums.TransactionType;
import com.nadson.myfinance.infrastructure.adapter.persistence.entity.CategoryJpaEntity;
import com.nadson.myfinance.infrastructure.adapter.persistence.repository.SpringCategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CategoryPersistenceAdapterTest {

    @Mock
    private SpringCategoryRepository repository;

    @InjectMocks
    private CategoryPersistenceAdapter adapter;

    private Category createCategory() {
        return new Category(UUID.randomUUID(), UUID.randomUUID(), "Lazer", "#000000", "Circle", TransactionType.EXPENSE);
    }

    @Test
    @DisplayName("Deve salvar categoria")
    void shouldSaveCategory() {
        Category category = createCategory();
        when(repository.save(any(CategoryJpaEntity.class))).thenReturn(new CategoryJpaEntity(category));

        Category result = adapter.save(category);

        assertThat(result).isNotNull();
        verify(repository).save(any(CategoryJpaEntity.class));
    }

    @Test
    @DisplayName("Deve buscar por ID")
    void shouldFindById() {
        UUID id = UUID.randomUUID();
        CategoryJpaEntity entity = mock(CategoryJpaEntity.class);
        when(entity.toDomain()).thenReturn(createCategory());
        when(repository.findById(id)).thenReturn(Optional.of(entity));

        assertThat(adapter.findById(id)).isNotNull();
    }

    @Test
    @DisplayName("Deve buscar por userId")
    void shouldFindAllByUserId() {
        UUID userId = UUID.randomUUID();
        CategoryJpaEntity entity = mock(CategoryJpaEntity.class);
        when(entity.toDomain()).thenReturn(createCategory());
        when(repository.findByUserId(userId)).thenReturn(List.of(entity));

        assertThat(adapter.findAllByUserId(userId)).hasSize(1);
    }

    @Test
    @DisplayName("Deve buscar por nome e userId")
    void shouldFindByNameAndUserId() {
        UUID userId = UUID.randomUUID();
        CategoryJpaEntity entity = mock(CategoryJpaEntity.class);
        when(entity.toDomain()).thenReturn(createCategory());
        when(repository.findByNameAndUserId("Lazer", userId)).thenReturn(Optional.of(entity));

        assertThat(adapter.findByNameAndUserId("Lazer", userId)).isNotNull();
    }

    @Test
    @DisplayName("Deve deletar categorias")
    void shouldDelete() {
        UUID id = UUID.randomUUID();
        adapter.deleteAllByUserId(id);
        adapter.deleteById(id);

        verify(repository).deleteAllByUserId(id);
        verify(repository).deleteById(id);
    }
}