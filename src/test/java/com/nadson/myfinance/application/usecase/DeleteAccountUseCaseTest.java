package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.out.*;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.enums.AccountType;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteAccountUseCaseTest {

    @Mock private AccountRepositoryPort accountRepo;
    @Mock private TransactionRepositoryPort transactionRepo;
    @Mock private RecurringTemplateRepositoryPort recurringRepo;
    @Mock private CreditCardRepositoryPort creditCardRepo;

    @InjectMocks
    private DeleteAccountUseCase useCase;

    @Test
    @DisplayName("Deve excluir conta e todos os vínculos quando usuário é o dono")
    void shouldDeleteAccountAndAllRelatedDataSuccessfully() {
        UUID userId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        Account account = new Account(accountId, userId, AccountType.CHECKING, "Inter", BigDecimal.ZERO);

        when(accountRepo.findById(accountId)).thenReturn(account);

        useCase.execute(accountId, userId);

        verify(creditCardRepo, times(1)).deleteAllByAccountId(accountId);
        verify(transactionRepo, times(1)).deleteAllByAccountId(accountId);
        verify(recurringRepo, times(1)).deleteAllByAccountId(accountId);
        verify(accountRepo, times(1)).deleteById(accountId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando a conta pertence a outro usuário")
    void shouldThrowExceptionWhenUserDoesNotOwnAccount() {
        UUID userId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        Account account = new Account(accountId, otherUserId, AccountType.CHECKING, "Fake Account", BigDecimal.ZERO);

        when(accountRepo.findById(accountId)).thenReturn(account);

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () ->
                useCase.execute(accountId, userId));

        assertEquals("Conta não encontrada ou acesso negado.", exception.getMessage());
        verify(accountRepo, never()).deleteById(any());
        verifyNoInteractions(transactionRepo, recurringRepo, creditCardRepo);
    }

    @Test
    @DisplayName("Deve lançar exceção quando a conta não existe")
    void shouldThrowExceptionWhenAccountDoesNotExist() {
        UUID userId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();

        when(accountRepo.findById(accountId)).thenReturn(null);

        assertThrows(BusinessRuleException.class, () -> useCase.execute(accountId, userId));

        verify(accountRepo, never()).deleteById(any());
    }
}