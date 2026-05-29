package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.out.*;
import com.nadson.myfinance.domain.entity.*;
import com.nadson.myfinance.domain.enums.AccountType;
import com.nadson.myfinance.domain.exception.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateCreditCardUseCaseTest {

    @Mock private CreditCardRepositoryPort repository;
    @Mock private AccountRepositoryPort accountRepository;
    @InjectMocks private CreateCreditCardUseCase useCase;

    @Test
    @DisplayName("Deve falhar se a conta não existir")
    void shouldFailWhenAccountNotFound() {
        UUID accId = UUID.randomUUID();
        when(accountRepository.findById(accId)).thenReturn(null);

        assertThrows(AccountNotFoundException.class, () ->
                useCase.execute(UUID.randomUUID(), "Nubank", BigDecimal.TEN, 1, 5, accId));
    }

    @Test
    @DisplayName("Deve falhar se a conta pertencer a outro usuário")
    void shouldFailWhenAccountDoesNotBelongToUser() {
        UUID userId = UUID.randomUUID();
        UUID accId = UUID.randomUUID();
        Account account = new Account(accId, UUID.randomUUID(), AccountType.CHECKING, "Acc", BigDecimal.ZERO);

        when(accountRepository.findById(accId)).thenReturn(account);

        assertThrows(BusinessRuleException.class, () ->
                useCase.execute(userId, "Nubank", BigDecimal.TEN, 1, 5, accId));
    }

    @Test
    @DisplayName("Deve criar cartão com sucesso")
    void shouldCreateCreditCardSuccessfully() {
        UUID userId = UUID.randomUUID();
        UUID accId = UUID.randomUUID();
        Account account = new Account(accId, userId, AccountType.CHECKING, "Acc", BigDecimal.ZERO);

        when(accountRepository.findById(accId)).thenReturn(account);
        when(repository.save(any(CreditCard.class))).thenAnswer(i -> i.getArgument(0));

        useCase.execute(userId, "Nubank", BigDecimal.TEN, 1, 5, accId);

        verify(repository).save(any(CreditCard.class));
    }
}