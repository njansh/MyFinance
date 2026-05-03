package com.nadson.myfinance.application.listener;

import com.nadson.myfinance.application.port.out.BudgetRepositoryPort;
import com.nadson.myfinance.domain.entity.Budget;
import com.nadson.myfinance.domain.event.TransactionCreatedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
 class BudgetAlertEventListenerTest {

    @Mock private BudgetRepositoryPort budgetRepository;
    @InjectMocks private BudgetAlertEventListener listener;

    @Test
    void shouldUpdateBudgetWhenTransactionEventIsReceived() {

        UUID userId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        TransactionCreatedEvent event = new TransactionCreatedEvent(userId, categoryId, new BigDecimal("100.00"), 5, 2026);

        Budget budget = new Budget(UUID.randomUUID(), userId, categoryId, 5, 2026, new BigDecimal("1000.00"));

        when(budgetRepository.findByUserIdAndCategoryIdAndMonthAndYear(userId, categoryId, 5, 2026))
                .thenReturn(budget);

        listener.handleTransactionCreated(event);


        verify(budgetRepository, times(1)).save(argThat(b ->
                b.getSpentAmount().compareTo(new BigDecimal("100.00")) == 0
        ));
    }
}