package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.out.CategoryRepositoryPort;
import com.nadson.myfinance.domain.entity.Category;
import com.nadson.myfinance.domain.enums.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetCategoriesUseCaseTest {

    @Mock
    private CategoryRepositoryPort categoryRepositoryPort;

    @InjectMocks
    private GetCategoriesUseCase useCase;

    @Test
    @DisplayName("Deve retornar a lista de categorias de um usuário específico")
    void shouldReturnCategoriesByUserId() {
        UUID userId = UUID.randomUUID();
        List<Category> categories = List.of(
                new Category(UUID.randomUUID(), userId, "Alimentação", "#FF0000", TransactionType.EXPENSE),
                new Category(UUID.randomUUID(), userId, "Salário", "#00FF00", TransactionType.INCOME)
        );

        when(categoryRepositoryPort.findAllByUserId(userId)).thenReturn(categories);

        List<Category> result = useCase.execute(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Alimentação", result.get(0).getName());
        verify(categoryRepositoryPort, times(1)).findAllByUserId(userId);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando o usuário não possui categorias")
    void shouldReturnEmptyListWhenUserHasNoCategories() {
        UUID userId = UUID.randomUUID();
        when(categoryRepositoryPort.findAllByUserId(userId)).thenReturn(List.of());

        List<Category> result = useCase.execute(userId);

        assertTrue(result.isEmpty());
        verify(categoryRepositoryPort, times(1)).findAllByUserId(userId);
    }
}