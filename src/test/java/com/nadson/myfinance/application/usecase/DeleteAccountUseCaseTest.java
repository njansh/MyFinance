package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.out.*;
import com.nadson.myfinance.domain.entity.*;
import com.nadson.myfinance.domain.enums.AccountType;
import com.nadson.myfinance.domain.exception.BusinessRuleException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteAccountUseCaseTest {

    @Mock private AccountRepositoryPort accountRepo;
    @Mock private TransactionRepositoryPort transactionRepo;
    @Mock private RecurringTemplateRepositoryPort recurringRepo;
    @Mock private CreditCardRepositoryPort creditCardRepo;
    @Mock private DeleteTransactionUseCase deleteTransactionUseCase;

    @InjectMocks
    private DeleteAccountUseCase useCase;

    @Test
    @DisplayName("Deve falhar se a conta não existir ou não pertencer ao usuário")
    void shouldFailWhenAccountNotFoundOrUnauthorized() {
        UUID accId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        // Simula conta inexistente (null)
        when(accountRepo.findById(accId)).thenReturn(null);

        assertThrows(BusinessRuleException.class, () -> useCase.execute(accId, userId));
    }

    @Test
    @DisplayName("Deve deletar conta e todas as dependências com sucesso")
    void shouldDeleteAccountAndDependencies() {
        UUID accId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Account account = new Account(accId, userId, AccountType.CHECKING, "Conta", BigDecimal.ZERO);

        // Setup das dependências
        Transaction t1 = new Transaction();
        t1.setTransactionId(UUID.randomUUID());

        when(accountRepo.findById(accId)).thenReturn(account);
        when(transactionRepo.findAllByAccountId(accId)).thenReturn(List.of(t1));

        useCase.execute(accId, userId);

        // Verifica deleção em cascata
        verify(deleteTransactionUseCase).execute(t1.getTransactionId());
        verify(creditCardRepo).deleteAllByAccountId(accId);
        verify(recurringRepo).deleteAllByAccountId(accId);
        verify(accountRepo).deleteById(accId);
    }
}