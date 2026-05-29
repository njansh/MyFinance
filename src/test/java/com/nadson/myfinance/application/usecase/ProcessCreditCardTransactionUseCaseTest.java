package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.out.*;
import com.nadson.myfinance.domain.entity.*;
import com.nadson.myfinance.domain.exception.BusinessRuleException;
import com.nadson.myfinance.domain.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessCreditCardTransactionUseCaseTest {

    @Mock private CreditCardRepositoryPort creditCardRepository;
    @Mock private BillingCycleRepositoryPort billingCycleRepository;
    @Mock private CreditCardPurchaseRepositoryPort purchaseRepository;
    @Mock private CreditCardInstallmentRepositoryPort installmentRepository;

    @InjectMocks
    private ProcessCreditCardTransactionUseCase useCase;

    private UUID userId;
    private UUID cardId;
    private CreditCard cardMock;
    private CreditCardPurchase purchaseMock;
    private BillingCycle cycleMock;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        cardId = UUID.randomUUID();
        cardMock = mock(CreditCard.class);
        purchaseMock = mock(CreditCardPurchase.class);
        cycleMock = mock(BillingCycle.class);

        // Configurações básicas padrão para evitar NullPointerExceptions nos fluxos comuns
        when(cardMock.getUserId()).thenReturn(userId);
        when(cardMock.getId()).thenReturn(cardId);
        when(purchaseMock.getId()).thenReturn(UUID.randomUUID());
        when(cycleMock.getId()).thenReturn(UUID.randomUUID());
    }

    @Test
    @DisplayName("1. Deve falhar se o cartão de crédito não for encontrado")
    void shouldFailWhenCreditCardNotFound() {
        when(creditCardRepository.findById(cardId)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () ->
                useCase.execute(userId, cardId, UUID.randomUUID(), "Compra", BigDecimal.TEN, LocalDate.now(), 1));
    }

    @Test
    @DisplayName("2. Deve falhar se o cartão pertencer a outro usuário")
    void shouldFailWhenCardBelongsToAnotherUser() {
        UUID intruderId = UUID.randomUUID();
        when(creditCardRepository.findById(cardId)).thenReturn(cardMock);
        when(cardMock.getUserId()).thenReturn(userId); // Dono é o userId, mas quem chama é intruderId

        assertThrows(ResourceNotFoundException.class, () ->
                useCase.execute(intruderId, cardId, UUID.randomUUID(), "Compra", BigDecimal.TEN, LocalDate.now(), 1));
    }

    @Test
    @DisplayName("3. Deve falhar se o limite disponível for insuficiente")
    void shouldFailWhenLimitIsInsufficient() {
        when(creditCardRepository.findById(cardId)).thenReturn(cardMock);
        when(cardMock.getCreditLimit()).thenReturn(new BigDecimal("500.00"));

        BillingCycle unpaidCycle = mock(BillingCycle.class);
        when(unpaidCycle.getTotalAmount()).thenReturn(new BigDecimal("450.00"));
        when(billingCycleRepository.findUnpaidCyclesByCardId(cardId)).thenReturn(List.of(unpaidCycle));

        assertThrows(BusinessRuleException.class, () ->
                useCase.execute(userId, cardId, UUID.randomUUID(), "Compra", new BigDecimal("100.00"), LocalDate.now(), 1));
    }

    @Test
    @DisplayName("4. Deve processar com sucesso usando um ciclo existente")
    void shouldProcessWithExistingCycle() {
        when(creditCardRepository.findById(cardId)).thenReturn(cardMock);
        when(cardMock.getCreditLimit()).thenReturn(new BigDecimal("1000.00"));
        when(billingCycleRepository.findUnpaidCyclesByCardId(cardId)).thenReturn(List.of());
        when(purchaseRepository.save(any())).thenReturn(purchaseMock);
        when(billingCycleRepository.findOpenCycleByCardId(eq(cardId), any())).thenReturn(cycleMock);
        when(billingCycleRepository.save(any())).thenReturn(cycleMock);

        useCase.execute(userId, cardId, UUID.randomUUID(), "Notebook", new BigDecimal("150.00"), LocalDate.now(), 3);

        verify(installmentRepository, times(3)).save(any());
        verify(billingCycleRepository, times(3)).save(any());
    }

    @Test
    @DisplayName("5. Deve criar novo ciclo se a data da transação for ANTES do fechamento")
    void shouldCreateCycleWhenTransactionIsBeforeClosingDay() {
        when(creditCardRepository.findById(cardId)).thenReturn(cardMock);
        when(cardMock.getCreditLimit()).thenReturn(new BigDecimal("1000.00"));
        when(cardMock.getClosingDay()).thenReturn(10);
        when(cardMock.getDueDay()).thenReturn(20);

        when(billingCycleRepository.findUnpaidCyclesByCardId(cardId)).thenReturn(List.of());
        when(purchaseRepository.save(any())).thenReturn(purchaseMock);
        when(billingCycleRepository.findOpenCycleByCardId(eq(cardId), any())).thenReturn(null); // Força criação
        when(billingCycleRepository.save(any())).thenReturn(cycleMock);

        LocalDate txDate = LocalDate.of(2026, 5, 5); // Dia 5 é antes do fechamento (10)
        useCase.execute(userId, cardId, UUID.randomUUID(), "Compra", new BigDecimal("50.00"), txDate, 1);

        ArgumentCaptor<BillingCycle> cycleCaptor = ArgumentCaptor.forClass(BillingCycle.class);
        verify(billingCycleRepository).save(cycleCaptor.capture());

        // Verifica se a lógica interna calculou o fechamento e vencimento para o próprio mês de maio
        assertThat(cycleCaptor.getValue().getClosingDate()).isEqualTo(LocalDate.of(2026, 5, 10));
        assertThat(cycleCaptor.getValue().getDueDate()).isEqualTo(LocalDate.of(2026, 5, 20));
    }

    @Test
    @DisplayName("6. Deve criar novo ciclo avançando o mês se a data for APÓS o fechamento")
    void shouldCreateCycleWhenTransactionIsAfterClosingDay() {
        when(creditCardRepository.findById(cardId)).thenReturn(cardMock);
        when(cardMock.getCreditLimit()).thenReturn(new BigDecimal("1000.00"));
        when(cardMock.getClosingDay()).thenReturn(10);
        when(cardMock.getDueDay()).thenReturn(20);

        when(billingCycleRepository.findUnpaidCyclesByCardId(cardId)).thenReturn(List.of());
        when(purchaseRepository.save(any())).thenReturn(purchaseMock);
        when(billingCycleRepository.findOpenCycleByCardId(eq(cardId), any())).thenReturn(null);
        when(billingCycleRepository.save(any())).thenReturn(cycleMock);

        LocalDate txDate = LocalDate.of(2026, 5, 15); // Dia 15 é após o fechamento (10)
        useCase.execute(userId, cardId, UUID.randomUUID(), "Compra", new BigDecimal("50.00"), txDate, 1);

        ArgumentCaptor<BillingCycle> cycleCaptor = ArgumentCaptor.forClass(BillingCycle.class);
        verify(billingCycleRepository).save(cycleCaptor.capture());

        // Verifica se o fechamento foi empurrado para junho
        assertThat(cycleCaptor.getValue().getClosingDate()).isEqualTo(LocalDate.of(2026, 6, 10));
    }

    @Test
    @DisplayName("7. Deve ajustar o vencimento para o mês seguinte se o dia de vencimento for menor ou igual ao de fechamento")
    void shouldAdjustDueDateToNextMonthWhenDueDayIsBeforeClosingDay() {
        when(creditCardRepository.findById(cardId)).thenReturn(cardMock);
        when(cardMock.getCreditLimit()).thenReturn(new BigDecimal("1000.00"));
        when(cardMock.getClosingDay()).thenReturn(25);
        when(cardMock.getDueDay()).thenReturn(5); // Vence dia 5, fecha dia 25

        when(billingCycleRepository.findUnpaidCyclesByCardId(cardId)).thenReturn(List.of());
        when(purchaseRepository.save(any())).thenReturn(purchaseMock);
        when(billingCycleRepository.findOpenCycleByCardId(eq(cardId), any())).thenReturn(null);
        when(billingCycleRepository.save(any())).thenReturn(cycleMock);

        LocalDate txDate = LocalDate.of(2026, 5, 10);
        useCase.execute(userId, cardId, UUID.randomUUID(), "Compra", new BigDecimal("50.00"), txDate, 1);

        ArgumentCaptor<BillingCycle> cycleCaptor = ArgumentCaptor.forClass(BillingCycle.class);
        verify(billingCycleRepository).save(cycleCaptor.capture());

        // Se fecha 25/05, tem que vencer em 05/06
        assertThat(cycleCaptor.getValue().getClosingDate()).isEqualTo(LocalDate.of(2026, 5, 25));
        assertThat(cycleCaptor.getValue().getDueDate()).isEqualTo(LocalDate.of(2026, 6, 5));
    }
}