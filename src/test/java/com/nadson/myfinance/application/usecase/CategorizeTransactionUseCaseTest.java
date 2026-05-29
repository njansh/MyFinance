package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.out.CategoryRepositoryPort;
import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
import com.nadson.myfinance.domain.entity.Category;
import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.domain.exception.CategoryNotFoundException;
import com.nadson.myfinance.domain.exception.TransactionNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CategorizeTransactionUseCaseTest {

    private TransactionRepositoryPort transactionRepository;
    private CategoryRepositoryPort categoryRepository;
    private CategorizeTransactionUseCase useCase;

    @BeforeEach
    void setUp() {
        transactionRepository = mock(TransactionRepositoryPort.class);
        categoryRepository = mock(CategoryRepositoryPort.class);
        useCase = new CategorizeTransactionUseCase(transactionRepository, categoryRepository);
    }

    @Test
    @DisplayName("Deve atualizar a categoria da transação com sucesso")
    void shouldCategorizeTransactionSuccessfully() {
        UUID txId = UUID.randomUUID();
        UUID catId = UUID.randomUUID();

        // Mock da transação e categoria
        Transaction tx = mock(Transaction.class);
        when(transactionRepository.findById(txId)).thenReturn(tx);
        when(categoryRepository.findById(catId)).thenReturn(mock(Category.class));
        when(transactionRepository.save(tx)).thenReturn(tx);

        useCase.execute(txId, catId);

        verify(tx).updateCategory(catId);
        verify(transactionRepository).save(tx);
    }

    @Test
    @DisplayName("Deve falhar quando transação não for encontrada")
    void shouldFailWhenTransactionNotFound() {
        when(transactionRepository.findById(any())).thenReturn(null);
        assertThrows(TransactionNotFoundException.class, () -> useCase.execute(UUID.randomUUID(), UUID.randomUUID()));
    }

    @Test
    @DisplayName("Deve falhar quando categoria não for encontrada")
    void shouldFailWhenCategoryNotFound() {
        UUID txId = UUID.randomUUID();
        UUID catId = UUID.randomUUID();

        when(transactionRepository.findById(txId)).thenReturn(mock(Transaction.class));
        when(categoryRepository.findById(catId)).thenReturn(null);

        assertThrows(CategoryNotFoundException.class, () -> useCase.execute(txId, catId));
    }
}