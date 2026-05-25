//package com.nadson.myfinance.application.usecase;
//
//import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
//import com.nadson.myfinance.domain.entity.Transaction;
//import com.nadson.myfinance.domain.enums.TransactionType;
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
//import java.util.List;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class GetTransactionUsecaseTest {
//
//    @Mock
//    private TransactionRepositoryPort transactionRepositoryPort;
//
//    @InjectMocks
//    private GetTransactionUsecase useCase;
//
//    @Test
//    @DisplayName("Deve retornar uma transação específica por ID")
//    void shouldReturnTransactionById() {
//        UUID transactionId = UUID.randomUUID();
//        Transaction transaction = new Transaction(transactionId, "Mercado", new BigDecimal("150.00"),
//                LocalDateTime.now(), TransactionType.EXPENSE, UUID.randomUUID(), null, false, null, null);
//
//        when(transactionRepositoryPort.findById(transactionId)).thenReturn(transaction);
//
//        Transaction result = useCase.execute(transactionId);
//
//        assertNotNull(result);
//        assertEquals(transactionId, result.getTransactionId());
//        verify(transactionRepositoryPort).findById(transactionId);
//    }
//
//    @Test
//    @DisplayName("Deve lançar exceção quando transação por ID não for encontrada")
//    void shouldThrowExceptionWhenIdNotFound() {
//        UUID id = UUID.randomUUID();
//        when(transactionRepositoryPort.findById(id)).thenReturn(null);
//
//        assertThrows(TransactionNotFoundException.class, () -> useCase.execute(id));
//    }
//
//    @Test
//    @DisplayName("Deve retornar lista de transações filtrada por descrição")
//    void shouldReturnTransactionsFilteredByDescription() {
//        UUID accountId = UUID.randomUUID();
//        List<Transaction> transactions = List.of(
//                new Transaction(UUID.randomUUID(), "Gasolina Shell", BigDecimal.TEN, LocalDateTime.now(), TransactionType.EXPENSE, accountId, null, false, null, null),
//                new Transaction(UUID.randomUUID(), "Supermercado", BigDecimal.TEN, LocalDateTime.now(), TransactionType.EXPENSE, accountId, null, false, null, null)
//        );
//
//        when(transactionRepositoryPort.findAllByAccountId(accountId)).thenReturn(transactions);
//
//        List<Transaction> result = useCase.execute(accountId, "shell");
//
//        assertEquals(1, result.size());
//        assertTrue(result.get(0).getDescription().contains("Shell"));
//    }
//
//    @Test
//    @DisplayName("Deve retornar todas as transações da conta quando descrição for nula ou vazia")
//    void shouldReturnAllTransactionsWhenDescriptionIsEmpty() {
//        UUID accountId = UUID.randomUUID();
//        when(transactionRepositoryPort.findAllByAccountId(accountId)).thenReturn(List.of(mock(Transaction.class), mock(Transaction.class)));
//
//        List<Transaction> result = useCase.execute(accountId, "");
//
//        assertEquals(2, result.size());
//        verify(transactionRepositoryPort).findAllByAccountId(accountId);
//    }
//}