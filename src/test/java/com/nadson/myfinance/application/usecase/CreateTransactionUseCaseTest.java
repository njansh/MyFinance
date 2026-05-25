//package com.nadson.myfinance.application.usecase;
//
//import com.nadson.myfinance.application.port.out.*;
//import com.nadson.myfinance.domain.entity.*;
//import com.nadson.myfinance.domain.enums.AccountType;
//import com.nadson.myfinance.domain.enums.TransactionType;
//import com.nadson.myfinance.domain.event.TransactionCreatedEvent; // <-- Import adicionado
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.context.ApplicationEventPublisher;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class CreateTransactionUseCaseTest {
//
//    @Mock private TransactionRepositoryPort transactionRepo;
//    @Mock private AccountRepositoryPort accountRepo;
//    @Mock private CategoryRepositoryPort categoryRepo;
//    @Mock private ApplicationEventPublisher eventPublisher;
//
//    @InjectMocks
//    private CreateTransactionUseCase useCase;
//
//    @Test
//    void shouldCreateExpenseTransactionAndUpdateAccountBalance() {
//        // Arrange (Preparar os dados de teste)
//        UUID accountId = UUID.randomUUID();
//        UUID categoryId = UUID.randomUUID();
//        Account account = new Account(accountId, UUID.randomUUID(), AccountType.CHECKING, "Conta Corrente", new BigDecimal("1000.00"));
//        Category category = new Category(categoryId, account.getUserId(), "Alimentação", "#000000", TransactionType.EXPENSE);
//
//        Transaction transaction = new Transaction(
//                UUID.randomUUID(), "Compra no Mercado", new BigDecimal("200.00"),
//                LocalDateTime.now(), TransactionType.EXPENSE, accountId, categoryId, false, null, null
//        );
//
//        when(accountRepo.findById(accountId)).thenReturn(account);
//        when(categoryRepo.findById(categoryId)).thenReturn(category);
//        when(transactionRepo.save(any(Transaction.class))).thenReturn(transaction);
//
//        // Act (Executar a ação)
//        Transaction result = useCase.execute(transaction);
//
//        // Assert (Verificar se a regra de negócio funcionou)
//        assertNotNull(result);
//
//        // Verifica se a atualização ATÔMICA foi chamada com o valor negativo (pois é uma despesa de 200)
//        verify(accountRepo, times(1)).updateBalanceAtomic(eq(accountId), eq(new BigDecimal("-200.00")));
//
//        // Garante que o sistema NÃO usou o save tradicional para evitar sobreposição de saldo
//        verify(accountRepo, never()).save(any());
//
//        // Verifica se o evento de Budget foi disparado passando a classe exata para evitar confusão do Mockito
//        verify(eventPublisher, times(1)).publishEvent(any(TransactionCreatedEvent.class));
//    }
//}