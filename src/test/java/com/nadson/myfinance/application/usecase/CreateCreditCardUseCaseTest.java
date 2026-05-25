//package com.nadson.myfinance.application.usecase;
//
//import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
//import com.nadson.myfinance.application.port.out.CreditCardRepositoryPort;
//import com.nadson.myfinance.domain.entity.Account;
//import com.nadson.myfinance.domain.entity.CreditCard;
//import com.nadson.myfinance.domain.enums.AccountType;
//import com.nadson.myfinance.domain.exception.AccountNotFoundException;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.math.BigDecimal;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class CreateCreditCardUseCaseTest {
//
//    @Mock
//    private CreditCardRepositoryPort repository;
//
//    @Mock
//    private AccountRepositoryPort accountRepository;
//
//    @InjectMocks
//    private CreateCreditCardUseCase useCase;
//
//    @Test
//    @DisplayName("Deve criar um cartão de crédito com sucesso quando a conta existe")
//    void shouldCreateCreditCardSuccessfully() {
//        UUID accountId = UUID.randomUUID();
//        String name = "Inter Black";
//        BigDecimal limit = new BigDecimal("5000.00");
//        int closingDay = 5;
//        int dueDay = 15;
//
//        when(accountRepository.findById(accountId)).thenReturn(mock(Account.class));
//        when(repository.save(any(CreditCard.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//        CreditCard result = useCase.execute(name, limit, closingDay, dueDay, accountId);
//
//        assertNotNull(result);
//        assertEquals(name, result.getName());
//        assertEquals(accountId, result.getAccountId());
//        assertEquals(limit, result.getCreditLimit());
//        assertEquals(closingDay, result.getClosingDay());
//        assertEquals(dueDay, result.getDueDay());
//        verify(repository, times(1)).save(any(CreditCard.class));
//    }
//
//    @Test
//    @DisplayName("Deve lançar exceção quando a conta vinculada não for encontrada")
//    void shouldThrowExceptionWhenAccountNotFound() {
//        UUID accountId = UUID.randomUUID();
//        when(accountRepository.findById(accountId)).thenReturn(null);
//
//        assertThrows(AccountNotFoundException.class, () ->
//                useCase.execute("Visa", BigDecimal.ONE, 1, 10, accountId));
//
//        verifyNoInteractions(repository);
//    }
//}