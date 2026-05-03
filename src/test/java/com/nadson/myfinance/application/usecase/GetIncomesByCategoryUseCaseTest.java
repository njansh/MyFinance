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
class GetIncomesByCategoryUseCaseTest {

    @Mock
    private TransactionRepositoryPort transactionRepository;

    @InjectMocks
    private GetIncomesByCategoryUseCase useCase;

    @Test
    @DisplayName("Deve retornar soma de receitas por categoria filtrando por período")
    void shouldReturnIncomesByCategoryWithDateFilter() {
        UUID accountId = UUID.randomUUID();
        LocalDateTime start = LocalDateTime.now().minusDays(30);
        LocalDateTime end = LocalDateTime.now();

        Map<String, BigDecimal> expectedMap = Map.of(
                "Salário", new BigDecimal("5000.00"),
                "Renda Extra", new BigDecimal("350.00")
        );

        when(transactionRepository.getSumByCategoryAndTypeAndDateBetween(
                eq(accountId), eq(TransactionType.INCOME), eq(start), eq(end)))
                .thenReturn(expectedMap);

        Map<String, BigDecimal> result = useCase.execute(accountId, start, end);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(new BigDecimal("5000.00"), result.get("Salário"));
        verify(transactionRepository, times(1))
                .getSumByCategoryAndTypeAndDateBetween(accountId, TransactionType.INCOME, start, end);
        verify(transactionRepository, never()).getSumByCategoryAndType(any(), any());
    }

    @Test
    @DisplayName("Deve retornar soma total de receitas por categoria quando datas forem nulas")
    void shouldReturnTotalIncomesByCategoryWhenDatesAreNull() {
        UUID accountId = UUID.randomUUID();
        Map<String, BigDecimal> expectedMap = Map.of("Investimentos", new BigDecimal("100.00"));

        when(transactionRepository.getSumByCategoryAndType(accountId, TransactionType.INCOME))
                .thenReturn(expectedMap);

        Map<String, BigDecimal> result = useCase.execute(accountId, null, null);

        assertNotNull(result);
        assertEquals(new BigDecimal("100.00"), result.get("Investimentos"));
        verify(transactionRepository, times(1)).getSumByCategoryAndType(accountId, TransactionType.INCOME);
        verify(transactionRepository, never()).getSumByCategoryAndTypeAndDateBetween(any(), any(), any(), any());
    }
}