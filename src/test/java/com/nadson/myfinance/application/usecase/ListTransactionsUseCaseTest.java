package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.domain.enums.AccountType;
import com.nadson.myfinance.domain.exception.AccountNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListTransactionsUseCaseTest {

    @Mock
    private AccountRepositoryPort accountRepositoryPort;

    @Mock
    private TransactionRepositoryPort transactionRepositoryPort;

    @InjectMocks
    private ListTransactionsUseCase useCase;

    private final UUID accountId = UUID.randomUUID();
    private final Pageable pageable = PageRequest.of(0, 10);
    private final Account mockAccount = new Account(accountId, UUID.randomUUID(), AccountType.CHECKING, "Inter", BigDecimal.ZERO);

    @Test
    @DisplayName("Deve listar transações apenas por ID da conta quando outros filtros são nulos")
    void shouldListByAccountIdOnly() {
        when(accountRepositoryPort.findById(accountId)).thenReturn(mockAccount);
        when(transactionRepositoryPort.findByAccountId(eq(accountId), any())).thenReturn(Page.empty());

        useCase.execute(accountId, null, null, null, pageable);

        verify(transactionRepositoryPort).findByAccountId(accountId, pageable);
        verify(transactionRepositoryPort, never()).findByAccountIdAndDateBetween(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Deve filtrar por data e descrição simultaneamente")
    void shouldFilterByDateAndDescription() {
        LocalDateTime start = LocalDateTime.now().minusDays(7);
        LocalDateTime end = LocalDateTime.now();
        String desc = "Netflix";

        when(accountRepositoryPort.findById(accountId)).thenReturn(mockAccount);
        when(transactionRepositoryPort.findByAccountIdAndDateBetweenAndDescription(eq(accountId), eq(start), eq(end), eq(desc), any()))
                .thenReturn(new PageImpl<>(List.of()));

        useCase.execute(accountId, start, end, desc, pageable);

        verify(transactionRepositoryPort).findByAccountIdAndDateBetweenAndDescription(accountId, start, end, desc, pageable);
    }

    @Test
    @DisplayName("Deve filtrar apenas por descrição quando datas são nulas")
    void shouldFilterByDescriptionOnly() {
        String desc = "Pix";

        when(accountRepositoryPort.findById(accountId)).thenReturn(mockAccount);
        when(transactionRepositoryPort.findByAccountIdAndDescription(eq(accountId), eq(desc), any()))
                .thenReturn(Page.empty());

        useCase.execute(accountId, null, null, desc, pageable);

        verify(transactionRepositoryPort).findByAccountIdAndDescription(accountId, desc, pageable);
    }

    @Test
    @DisplayName("Deve lançar AccountNotFoundException quando a conta não existir")
    void shouldThrowExceptionWhenAccountNotFound() {
        when(accountRepositoryPort.findById(accountId)).thenReturn(null);

        assertThrows(AccountNotFoundException.class, () ->
                useCase.execute(accountId, null, null, null, pageable));

        verifyNoInteractions(transactionRepositoryPort);
    }
}