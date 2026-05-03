package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.out.BudgetRepositoryPort;
import com.nadson.myfinance.application.port.out.CategoryRepositoryPort;
import com.nadson.myfinance.application.port.out.UserRepositoryPort;
import com.nadson.myfinance.domain.entity.Budget;
import com.nadson.myfinance.domain.entity.Category;
import com.nadson.myfinance.domain.entity.User;
import com.nadson.myfinance.domain.exception.BusinessRuleException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
 class CreateBudgetUseCaseTest {

    @Mock
    private BudgetRepositoryPort repository;

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private CategoryRepositoryPort categoryRepository;

    @InjectMocks
    private CreateBudgetUseCase useCase;

    @Test
    @DisplayName("Deve criar um orçamento com sucesso quando usuário e categoria existem")
    void shouldCreateBudgetSuccessfully() {
        UUID userId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        BigDecimal limit = new BigDecimal("1000.00");

        when(userRepository.findById(userId)).thenReturn(mock(User.class));
        when(categoryRepository.findById(categoryId)).thenReturn(mock(Category.class));
        when(repository.save(any(Budget.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Budget result = useCase.execute(userId, categoryId, 5, 2026, limit);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(categoryId, result.getCategoryId());
        assertEquals(limit, result.getLimitAmount());
        verify(repository, times(1)).save(any(Budget.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando o usuário não existe")
    void shouldThrowExceptionWhenUserNotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(null);

        assertThrows(BusinessRuleException.class, () ->
                useCase.execute(userId, UUID.randomUUID(), 5, 2026, BigDecimal.TEN));

        verifyNoInteractions(categoryRepository);
        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("Deve lançar exceção quando a categoria não existe")
    void shouldThrowExceptionWhenCategoryNotFound() {
        UUID userId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(mock(User.class));
        when(categoryRepository.findById(categoryId)).thenReturn(null);

        assertThrows(BusinessRuleException.class, () ->
                useCase.execute(userId, categoryId, 5, 2026, BigDecimal.TEN));

        verifyNoInteractions(repository);
    }
}