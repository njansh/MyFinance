package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.out.CategoryRepositoryPort;
import com.nadson.myfinance.domain.entity.Category;
import com.nadson.myfinance.domain.exception.BusinessRuleException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteCategoryUseCaseTest {

    @Mock
    private CategoryRepositoryPort repository;

    @InjectMocks
    private DeleteCategoryUseCase useCase;

    @Test
    @DisplayName("Deve deletar categoria com sucesso")
    void shouldDeleteCategorySuccessfully() {
        UUID userId = UUID.randomUUID();
        UUID catId = UUID.randomUUID();
        Category category = mock(Category.class);

        when(category.getUserId()).thenReturn(userId);
        when(repository.findById(catId)).thenReturn(category);

        useCase.execute(userId, catId);

        verify(repository).deleteById(catId);
    }

    @Test
    @DisplayName("Deve falhar se a categoria não for encontrada")
    void shouldFailWhenCategoryNotFound() {
        UUID catId = UUID.randomUUID();
        when(repository.findById(catId)).thenReturn(null);

        assertThrows(BusinessRuleException.class, () ->
                useCase.execute(UUID.randomUUID(), catId));
    }

    @Test
    @DisplayName("Deve falhar se o usuário não for dono da categoria")
    void shouldFailWhenUnauthorized() {
        UUID ownerId = UUID.randomUUID();
        UUID intruderId = UUID.randomUUID();
        UUID catId = UUID.randomUUID();
        Category category = mock(Category.class);

        when(category.getUserId()).thenReturn(ownerId);
        when(repository.findById(catId)).thenReturn(category);

        assertThrows(BusinessRuleException.class, () ->
                useCase.execute(intruderId, catId));
    }
}