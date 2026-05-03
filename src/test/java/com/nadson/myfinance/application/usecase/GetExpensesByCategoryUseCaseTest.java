package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
import com.nadson.myfinance.domain.enums.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
    class GetExpensesByCategoryUseCaseTest {

    @Mock
    private TransactionRepositoryPort transactionRepository;

    @InjectMocks
    private GetExpensesByCategoryUseCase useCase;

    @Test
    @DisplayName("Deve retornar soma por categoria filtrando por período quando as datas são informadas")
    void shouldReturnSumByCategoryWithDateFilter() {
        UUID accountId = UUID.randomUUID();
        LocalDateTime start = LocalDateTime.now().minusDays(30);
        LocalDateTime end = LocalDateTime.now();

        Map<String, BigDecimal> expectedMap = Map.of(
                "Alimentação", new BigDecimal("150.00"),
                "Transporte", new BigDecimal("50.00")
        );

        when(transactionRepository.getSumByCategoryAndTypeAndDateBetween(
                eq(accountId), eq(TransactionType.EXPENSE), eq(start), eq(end)))
                .thenReturn(expectedMap);

        Map<String, BigDecimal> result = useCase.execute(accountId, start, end);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(new BigDecimal("150.00"), result.get("Alimentação"));
        verify(transactionRepository, times(1))
                .getSumByCategoryAndTypeAndDateBetween(accountId, TransactionType.EXPENSE, start, end);
        verify(transactionRepository, never()).getSumByCategoryAndType(any(), any());
    }

    @Test
    @DisplayName("Deve retornar soma total por categoria quando as datas não são informadas")
    void shouldReturnTotalSumByCategoryWhenDatesAreNull() {
        UUID accountId = UUID.randomUUID();
        Map<String, BigDecimal> expectedMap = Map.of("Lazer", new BigDecimal("200.00"));

        when(transactionRepository.getSumByCategoryAndType(accountId, TransactionType.EXPENSE))
                .thenReturn(expectedMap);

        Map<String, BigDecimal> result = useCase.execute(accountId, null, null);

        assertNotNull(result);
        assertEquals(new BigDecimal("200.00"), result.get("Lazer"));
        verify(transactionRepository, times(1)).getSumByCategoryAndType(accountId, TransactionType.EXPENSE);
        verify(transactionRepository, never()).getSumByCategoryAndTypeAndDateBetween(any(), any(), any(), any());
    }
}