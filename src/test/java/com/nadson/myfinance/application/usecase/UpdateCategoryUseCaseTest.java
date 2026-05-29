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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpdateCategoryUseCaseTest {

    @Mock
    private CategoryRepositoryPort categoryRepositoryPort;

    @InjectMocks
    private UpdateCategoryUseCase useCase;

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando a categoria não for encontrada (existing == null)")
    void shouldThrowExceptionWhenCategoryNotFound() {
        UUID categoryId = UUID.randomUUID();
        when(categoryRepositoryPort.findById(categoryId)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () ->
                useCase.execute(UUID.randomUUID(), categoryId, "Alimentação", "#FF5733", "fastfood", TransactionType.EXPENSE));

        verify(categoryRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando a categoria pertencer a outro usuário (!existing.getUserId().equals(userId))")
    void shouldThrowExceptionWhenCategoryBelongsToAnotherUser() {
        UUID userId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        Category categoryMock = mock(Category.class);

        when(categoryRepositoryPort.findById(categoryId)).thenReturn(categoryMock);
        when(categoryMock.getUserId()).thenReturn(UUID.randomUUID()); // Retorna um ID diferente do userId

        assertThrows(IllegalArgumentException.class, () ->
                useCase.execute(userId, categoryId, "Alimentação", "#FF5733", "fastfood", TransactionType.EXPENSE));

        verify(categoryRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar e salvar a categoria com sucesso")
    void shouldUpdateCategorySuccessfully() {
        UUID userId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        Category categoryMock = mock(Category.class);

        when(categoryRepositoryPort.findById(categoryId)).thenReturn(categoryMock);
        when(categoryMock.getUserId()).thenReturn(userId);
        when(categoryMock.getCategoryId()).thenReturn(categoryId);

        when(categoryRepositoryPort.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Category result = useCase.execute(userId, categoryId, "Mercado", "#00FF00", "shop", TransactionType.EXPENSE);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Mercado");
        verify(categoryRepositoryPort, times(1)).save(any(Category.class));
    }
}