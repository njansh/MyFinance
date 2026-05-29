package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.out.*;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.enums.AccountType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteUserUseCaseTest {

    @Mock private UserRepositoryPort userRepo;
    @Mock private AccountRepositoryPort accountRepo;
    @Mock private CategoryRepositoryPort categoryRepo;
    @Mock private BudgetRepositoryPort budgetRepo;
    @Mock private GoalRepositoryPort goalRepo;
    @Mock private RecurringTemplateRepositoryPort recurringRepo;
    @Mock private TransactionRepositoryPort transactionRepo;
    @Mock private BillingCycleRepositoryPort billingCycleRepo;
    @Mock private BillingPaymentRepositoryPort billingPaymentRepo;
    @Mock private CreditCardRepositoryPort creditCardRepo;

    @InjectMocks
    private DeleteUserUseCase useCase;

    @Test
    @DisplayName("Deve deletar todos os dados do usuário em cascata")
    void shouldDeleteAllUserData() {
        UUID userId = UUID.randomUUID();
        UUID accId = UUID.randomUUID();
        Account account = new Account(accId, userId, AccountType.CHECKING, "Conta", BigDecimal.ZERO);

        when(accountRepo.findByUserId(userId)).thenReturn(List.of(account));

        useCase.execute(userId);

        // Verifica limpeza de faturamento
        verify(billingPaymentRepo).deleteAllByUserId(userId);
        verify(billingCycleRepo).deleteAllByUserId(userId);

        // Verifica limpeza vinculada às contas
        verify(transactionRepo).deleteAllByAccountId(accId);
        verify(recurringRepo).deleteAllByAccountId(accId);
        verify(creditCardRepo).deleteAllByAccountId(accId);
        verify(accountRepo).deleteById(accId);

        // Verifica limpeza geral do usuário
        verify(recurringRepo).deleteAllByUserId(userId);
        verify(goalRepo).deleteAllByUserId(userId);
        verify(budgetRepo).deleteAllByUserId(userId);
        verify(categoryRepo).deleteAllByUserId(userId);
        verify(userRepo).deleteById(userId);
    }
}