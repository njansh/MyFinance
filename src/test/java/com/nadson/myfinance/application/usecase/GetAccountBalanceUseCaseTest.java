package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.domain.enums.AccountType;
import com.nadson.myfinance.domain.enums.TransactionType;
import com.nadson.myfinance.infrastructure.adapter.web.dto.response.BalanceResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAccountBalanceUseCaseTest {

    @Mock private TransactionRepositoryPort transactionRepo;
    @Mock private AccountRepositoryPort accountRepo;

    @InjectMocks private GetAccountBalanceUseCase useCase;

    @Test
    @DisplayName("Should calculate balance response correctly with filtered dates")
    void shouldCalculateBalanceWithDates() {
        UUID accountId = UUID.randomUUID();
        Account account = new Account(accountId, UUID.randomUUID(), AccountType.CHECKING, "Conta", new BigDecimal("1000.00"));

        Transaction t1 = new Transaction();
        t1.setType(TransactionType.INCOME);
        t1.setAmount(new BigDecimal("500.00"));

        Transaction t2 = new Transaction();
        t2.setType(TransactionType.EXPENSE);
        t2.setAmount(new BigDecimal("200.00"));

        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();

        when(accountRepo.findById(accountId)).thenReturn(account);
        when(transactionRepo.findAllByAccountIdAndDateBetween(accountId, start, end)).thenReturn(List.of(t1, t2));

        BalanceResponse response = useCase.execute(accountId, start, end);

        assertThat(response.getIncomes()).isEqualByComparingTo("500.00");
        assertThat(response.getExpenses()).isEqualByComparingTo("200.00");
        assertThat(response.getBalance()).isEqualByComparingTo("1000.00"); // Saldo da conta não é afetado pelo filtro
    }
}