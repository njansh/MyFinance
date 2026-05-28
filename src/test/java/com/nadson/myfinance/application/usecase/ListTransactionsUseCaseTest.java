package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.entity.Transaction;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListTransactionsUseCaseTest {

    @Mock private AccountRepositoryPort accountRepository;
    @Mock private TransactionRepositoryPort transactionRepository;

    @InjectMocks
    private ListTransactionsUseCase useCase;

    @Test
    @DisplayName("Deve falhar se a conta não existir")
    void shouldFailWhenAccountNotFound() {
        UUID accId = UUID.randomUUID();
        when(accountRepository.findById(accId)).thenReturn(null);
        assertThrows(AccountNotFoundException.class, () -> useCase.execute(accId, null, null, null, Pageable.unpaged()));
    }

    @Test
    @DisplayName("Deve listar transações com todos os filtros (data e descrição)")
    void shouldListTransactionsWithDateAndDescription() {
        UUID accId = UUID.randomUUID();
        Pageable page = PageRequest.of(0, 10);
        when(accountRepository.findById(accId)).thenReturn(mock(Account.class));
        when(transactionRepository.findByAccountIdAndDateBetweenAndDescription(eq(accId), any(), any(), eq("teste"), eq(page)))
                .thenReturn(new PageImpl<>(List.of(mock(Transaction.class))));

        Page<Transaction> result = useCase.execute(accId, LocalDateTime.now().minusDays(1), LocalDateTime.now(), "teste", page);

        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("Deve listar apenas com filtro de data")
    void shouldListTransactionsWithDateOnly() {
        UUID accId = UUID.randomUUID();
        when(accountRepository.findById(accId)).thenReturn(mock(Account.class));
        when(transactionRepository.findByAccountIdAndDateBetween(eq(accId), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of()));

        useCase.execute(accId, LocalDateTime.now().minusDays(1), LocalDateTime.now(), null, Pageable.unpaged());
        verify(transactionRepository).findByAccountIdAndDateBetween(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Deve listar sem filtros extras")
    void shouldListTransactionsWithoutFilters() {
        UUID accId = UUID.randomUUID();
        when(accountRepository.findById(accId)).thenReturn(mock(Account.class));
        when(transactionRepository.findByAccountId(eq(accId), any()))
                .thenReturn(new PageImpl<>(List.of()));

        useCase.execute(accId, null, null, null, Pageable.unpaged());
        verify(transactionRepository).findByAccountId(eq(accId), any());
    }
    @Test
    @DisplayName("Deve listar apenas com filtro de descrição")
    void shouldListTransactionsWithDescriptionOnly() {
        UUID accId = UUID.randomUUID();
        Pageable page = PageRequest.of(0, 10);
        when(accountRepository.findById(accId)).thenReturn(mock(Account.class));
        when(transactionRepository.findByAccountIdAndDescription(eq(accId), eq("pagamento"), eq(page)))
                .thenReturn(new PageImpl<>(List.of(mock(Transaction.class))));

        Page<Transaction> result = useCase.execute(accId, null, null, "pagamento", page);

        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(transactionRepository).findByAccountIdAndDescription(eq(accId), eq("pagamento"), eq(page));
    }
    @Test
    @DisplayName("Deve cair no fluxo sem filtro de data se apenas uma das datas for fornecida")
    void shouldFallbackWhenOnlyOneDateIsProvided() {
        UUID accId = UUID.randomUUID();
        when(accountRepository.findById(accId)).thenReturn(mock(Account.class));
        when(transactionRepository.findByAccountId(eq(accId), any()))
                .thenReturn(new PageImpl<>(List.of()));
        useCase.execute(accId, LocalDateTime.now(), null, null, Pageable.unpaged());

        verify(transactionRepository).findByAccountId(eq(accId), any());
    }
}