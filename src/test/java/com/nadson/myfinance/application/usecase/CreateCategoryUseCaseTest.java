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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateCategoryUseCaseTest {

    @Mock
    private CategoryRepositoryPort categoryRepositoryPort;

    @InjectMocks
    private CreateCategoryUseCase useCase;

    @Test
    @DisplayName("Deve criar uma categoria com sucesso")
    void shouldCreateCategorySuccessfully() {
        UUID userId = UUID.randomUUID();
        String name = "Lazer";
        String color = "#00FF00";
        TransactionType type = TransactionType.EXPENSE;

        when(categoryRepositoryPort.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Category result = useCase.execute(userId, name, color, type);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(name, result.getName());
        assertEquals(color, result.getColorHex());
        assertEquals(type, result.getType());

        verify(categoryRepositoryPort, times(1)).save(any(Category.class));
    }
}