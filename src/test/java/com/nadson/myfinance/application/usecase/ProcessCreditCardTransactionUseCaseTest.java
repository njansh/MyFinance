package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.out.*;
import com.nadson.myfinance.domain.entity.CreditCard;
import com.nadson.myfinance.domain.entity.BillingCycle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessCreditCardTransactionUseCaseTest {

    @Mock private CreditCardRepositoryPort cardRepo;
    @Mock private BillingCycleRepositoryPort cycleRepo;

    @InjectMocks private ProcessCreditCardTransactionUseCase useCase;

    @Test
    void shouldCreateCorrectInstallmentsForCreditCardPurchase() {
        // Arrange
        UUID cardId = UUID.randomUUID();
        CreditCard card = new CreditCard(cardId, UUID.randomUUID(), "Visa", new BigDecimal("5000.00"), 5, 10);

        when(cardRepo.findById(cardId)).thenReturn(card);
        // Simula que não há faturas abertas para simplificar o teste
        when(cycleRepo.findOpenCycleByCardId(eq(cardId), any())).thenReturn(null);

        // Act: Compra de 100.00 em 2 parcelas
        useCase.execute(cardId, new BigDecimal("100.00"), LocalDate.now(), 2);

        // Assert
        // Deve salvar 2 ciclos de faturamento (ou atualizar 2 vezes o saldo das faturas)
        verify(cycleRepo, times(2)).save(any(BillingCycle.class));
    }
}