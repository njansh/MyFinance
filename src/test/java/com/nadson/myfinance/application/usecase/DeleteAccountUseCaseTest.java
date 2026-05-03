package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.out.*;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.enums.AccountType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteAccountUseCaseTest {

    @Mock private AccountRepositoryPort accountRepo;
    @Mock private TransactionRepositoryPort transactionRepo;
    @Mock private RecurringTemplateRepositoryPort recurringRepo;
    @Mock private CreditCardRepositoryPort creditCardRepo;

    @InjectMocks private DeleteAccountUseCase useCase;

    @Test
    void shouldDeleteAllRelatedDataWhenAccountIsDeleted() {
        UUID userId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        Account account = new Account(accountId, userId, AccountType.CHECKING, "Teste", BigDecimal.ZERO);

        when(accountRepo.findById(accountId)).thenReturn(account);

        useCase.execute(accountId, userId);

        verify(creditCardRepo).deleteAllByAccountId(accountId);
        verify(transactionRepo).deleteAllByAccountId(accountId);
        verify(recurringRepo).deleteAllByAccountId(accountId);
        verify(accountRepo).deleteById(accountId);
    }
}