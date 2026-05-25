//package com.nadson.myfinance.application.usecase;
//
//import com.nadson.myfinance.application.port.out.CategoryRepositoryPort;
//import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
//import com.nadson.myfinance.domain.entity.Category;
//import com.nadson.myfinance.domain.entity.Transaction;
//import com.nadson.myfinance.domain.enums.TransactionType;
//import com.nadson.myfinance.domain.exception.CategoryNotFoundException;
//import com.nadson.myfinance.domain.exception.TransactionNotFoundException;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class CategorizeTransactionUseCaseTest {
//
//    @Mock
//    private TransactionRepositoryPort transactionRepositoryPort;
//
//    @Mock
//    private CategoryRepositoryPort categoryRepositoryPort;
//
//    @InjectMocks
//    private CategorizeTransactionUseCase useCase;
//
//    @Test
//    @DisplayName("Deve atualizar a categoria de uma transação com sucesso")
//    void shouldCategorizeTransactionSuccessfully() {
//        UUID transactionId = UUID.randomUUID();
//        UUID oldCategoryId = UUID.randomUUID();
//        UUID newCategoryId = UUID.randomUUID();
//        UUID userId = UUID.randomUUID();
//        UUID accountId = UUID.randomUUID();
//
//        Transaction transaction = new Transaction(
//                transactionId, "Compra Teste", new BigDecimal("100.00"),
//                LocalDateTime.now(), TransactionType.EXPENSE, accountId,
//                oldCategoryId, false, null, new BigDecimal("500.00")
//        );
//
//        Category category = new Category(newCategoryId, userId, "Alimentação", "#FF5733", TransactionType.EXPENSE);
//
//        when(transactionRepositoryPort.findById(transactionId)).thenReturn(transaction);
//        when(categoryRepositoryPort.findById(newCategoryId)).thenReturn(category);
//        when(transactionRepositoryPort.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//        Transaction result = useCase.execute(transactionId, newCategoryId);
//
//        assertNotNull(result);
//        assertEquals(newCategoryId, result.getCategoryId());
//        verify(transactionRepositoryPort, times(1)).save(transaction);
//    }
//
//    @Test
//    @DisplayName("Deve lançar exceção quando a transação não for encontrada")
//    void shouldThrowExceptionWhenTransactionNotFound() {
//        UUID transactionId = UUID.randomUUID();
//        UUID categoryId = UUID.randomUUID();
//
//        when(transactionRepositoryPort.findById(transactionId)).thenReturn(null);
//
//        assertThrows(TransactionNotFoundException.class, () -> useCase.execute(transactionId, categoryId));
//        verify(categoryRepositoryPort, never()).findById(any());
//        verify(transactionRepositoryPort, never()).save(any());
//    }
//
//    @Test
//    @DisplayName("Deve lançar exceção quando a categoria não for encontrada")
//    void shouldThrowExceptionWhenCategoryNotFound() {
//        UUID transactionId = UUID.randomUUID();
//        UUID categoryId = UUID.randomUUID();
//        Transaction transaction = mock(Transaction.class);
//
//        when(transactionRepositoryPort.findById(transactionId)).thenReturn(transaction);
//        when(categoryRepositoryPort.findById(categoryId)).thenReturn(null);
//
//        assertThrows(CategoryNotFoundException.class, () -> useCase.execute(transactionId, categoryId));
//        verify(transactionRepositoryPort, never()).save(any());
//    }
//}