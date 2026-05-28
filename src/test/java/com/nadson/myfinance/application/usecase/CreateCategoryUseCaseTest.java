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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateCategoryUseCaseTest {

    @Mock private CategoryRepositoryPort repository;
    @InjectMocks private CreateCategoryUseCase useCase;

    @Test
    @DisplayName("Deve criar categoria com sucesso")
    void shouldCreateCategorySuccessfully() {
        UUID userId = UUID.randomUUID();
        when(repository.save(any(Category.class))).thenAnswer(i -> i.getArgument(0));

        useCase.execute(userId, "Alimentação", "#FFFFFF", "icon", TransactionType.EXPENSE);

        verify(repository, times(1)).save(any(Category.class));
    }
}