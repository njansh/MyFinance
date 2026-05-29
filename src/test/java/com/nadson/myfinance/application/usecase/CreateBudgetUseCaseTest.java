	package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.out.*;
import com.nadson.myfinance.domain.entity.*;
import com.nadson.myfinance.domain.enums.TransactionType;
import com.nadson.myfinance.domain.exception.BusinessRuleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CreateBudgetUseCaseTest {

    private BudgetRepositoryPort repository;
    private UserRepositoryPort userRepository;
    private CategoryRepositoryPort categoryRepository;
    private TransactionRepositoryPort transactionRepository;
    private CreateBudgetUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = mock(BudgetRepositoryPort.class);
        userRepository = mock(UserRepositoryPort.class);
        categoryRepository = mock(CategoryRepositoryPort.class);
        transactionRepository = mock(TransactionRepositoryPort.class);
        useCase = new CreateBudgetUseCase(repository, userRepository, categoryRepository, transactionRepository);
    }

    @Test
    @DisplayName("Deve criar orçamento com sucesso calculando gastos existentes")
    void shouldCreateBudgetSuccessfully() {
        UUID userId = UUID.randomUUID();
        UUID catId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(mock(User.class));
        when(categoryRepository.findById(catId)).thenReturn(mock(Category.class));

        // Simula transações de despesa (soma 50 + 30 = 80)
        Transaction t1 = mock(Transaction.class);
        when(t1.getType()).thenReturn(TransactionType.EXPENSE);
        when(t1.getAmount()).thenReturn(new BigDecimal("50.00"));

        Transaction t2 = mock(Transaction.class);
        when(t2.getType()).thenReturn(TransactionType.EXPENSE);
        when(t2.getAmount()).thenReturn(new BigDecimal("30.00"));

        when(transactionRepository.findAllByUserIdAndCategoryIdAndMonthAndYear(userId, catId, 5, 2026))
                .thenReturn(List.of(t1, t2));

        when(repository.save(any(Budget.class))).thenAnswer(i -> i.getArgument(0));

        Budget budget = useCase.execute(userId, catId, 5, 2026, new BigDecimal("100.00"));

        assertThat(budget.getSpentAmount()).isEqualByComparingTo("80.00");
        verify(repository).save(any(Budget.class));
    }

    @Test
    @DisplayName("Deve falhar se usuário não for encontrado")
    void shouldFailWhenUserNotFound() {
        when(userRepository.findById(any())).thenReturn(null);
        assertThrows(BusinessRuleException.class, () ->
                useCase.execute(UUID.randomUUID(), UUID.randomUUID(), 5, 2026, BigDecimal.TEN));
    }

    @Test
    @DisplayName("Deve falhar se categoria não for encontrada")
    void shouldFailWhenCategoryNotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(mock(User.class));
        when(categoryRepository.findById(any())).thenReturn(null);

        assertThrows(BusinessRuleException.class, () ->
                useCase.execute(userId, UUID.randomUUID(), 5, 2026, BigDecimal.TEN));
    }
    
}