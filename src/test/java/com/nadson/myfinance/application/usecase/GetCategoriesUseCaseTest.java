package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.out.CategoryRepositoryPort;
import com.nadson.myfinance.domain.entity.Category;
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
class GetCategoriesUseCaseTest {

    @Mock
    private CategoryRepositoryPort categoryRepositoryPort;

    @InjectMocks
    private GetCategoriesUseCase useCase;

    @Test
    @DisplayName("Deve retornar a lista de categorias do usuário")
    void shouldReturnCategoriesByUserId() {
        UUID userId = UUID.randomUUID();
        List<Category> expectedCategories = List.of(mock(Category.class), mock(Category.class));

        when(categoryRepositoryPort.findAllByUserId(userId)).thenReturn(expectedCategories);

        List<Category> result = useCase.execute(userId);

        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(expectedCategories);
        verify(categoryRepositoryPort, times(1)).findAllByUserId(userId);
    }
}