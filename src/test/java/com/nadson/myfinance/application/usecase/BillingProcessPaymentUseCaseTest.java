package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.ProcessTransactionInGoalPort;
import com.nadson.myfinance.application.port.out.*;
import com.nadson.myfinance.domain.entity.*;
import com.nadson.myfinance.domain.enums.AccountType;
import com.nadson.myfinance.domain.enums.BillingCycleStatus;
import com.nadson.myfinance.domain.enums.InstallmentStatus;
import com.nadson.myfinance.domain.enums.TransactionType;
import com.nadson.myfinance.domain.exception.BusinessRuleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class BillingProcessPaymentUseCaseTest {

    private CreditCardInstallmentRepositoryPort installmentRepository;
    private BillingPaymentRepositoryPort paymentRepository;
    private AccountRepositoryPort accountRepository;
    private BillingCycleRepositoryPort billingCycleRepository;
    private CreditCardRepositoryPort creditCardRepository;
    private TransactionRepositoryPort transactionRepository;
    private CategoryRepositoryPort categoryRepository;
    private ProcessTransactionInGoalPort processTransactionInGoal;

    private BillingProcessPaymentUseCase useCase;

    @BeforeEach
    void setUp() {
        installmentRepository = mock(CreditCardInstallmentRepositoryPort.class);
        paymentRepository = mock(BillingPaymentRepositoryPort.class);
        accountRepository = mock(AccountRepositoryPort.class);
        billingCycleRepository = mock(BillingCycleRepositoryPort.class);
        creditCardRepository = mock(CreditCardRepositoryPort.class);
        transactionRepository = mock(TransactionRepositoryPort.class);
        categoryRepository = mock(CategoryRepositoryPort.class);
        processTransactionInGoal = mock(ProcessTransactionInGoalPort.class);

        useCase = new BillingProcessPaymentUseCase(
                installmentRepository, paymentRepository, accountRepository,
                billingCycleRepository, creditCardRepository, transactionRepository,
                categoryRepository, processTransactionInGoal
        );
    }

    @Test
    @DisplayName("Deve falhar ao processar pagamento se conta não pertencer ao usuário")
    void shouldFailWhenAccountDoesNotBelongToUser() {
        UUID userId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        Account wrongAccount = new Account(accountId, UUID.randomUUID(), AccountType.CHECKING, "Conta", BigDecimal.ZERO);

        when(accountRepository.findById(accountId)).thenReturn(wrongAccount);

        assertThrows(BusinessRuleException.class, () ->
                useCase.BillingProcessPayment(userId, UUID.randomUUID(), UUID.randomUUID(), accountId, BigDecimal.TEN));
    }

    @Test
    @DisplayName("Deve validar a existência do ciclo de faturamento")
    void shouldFailWhenBillingCycleNotFound() {
        UUID userId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        Account account = new Account(accountId, userId, AccountType.CHECKING, "Conta", BigDecimal.TEN);

        when(accountRepository.findById(accountId)).thenReturn(account);
        when(billingCycleRepository.findById(any())).thenReturn(null);

        assertThrows(BusinessRuleException.class, () ->
                useCase.BillingProcessPayment(userId, UUID.randomUUID(), UUID.randomUUID(), accountId, BigDecimal.TEN));
    }

    @Test
    @DisplayName("Deve falhar se o ciclo de faturamento não pertencer ao cartão")
    void shouldFailWhenCycleDoesNotBelongToCard() {
        UUID userId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();
        UUID wrongCardId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();

        Account account = new Account(accountId, userId, AccountType.CHECKING, "Conta", BigDecimal.TEN);
        BillingCycle cycle = new BillingCycle(null, wrongCardId, LocalDate.now(), LocalDate.now().plusDays(10), LocalDate.now().plusDays(20), BigDecimal.TEN, BillingCycleStatus.OPEN);

        when(accountRepository.findById(accountId)).thenReturn(account);
        when(billingCycleRepository.findById(any())).thenReturn(cycle);

        assertThrows(BusinessRuleException.class, () ->
                useCase.BillingProcessPayment(userId, cardId, UUID.randomUUID(), accountId, BigDecimal.TEN));
    }

    @Test
    @DisplayName("Deve processar pagamento com sucesso (Caminho Feliz)")
    void shouldProcessPaymentSuccessfully() {
        UUID userId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        UUID cycleId = UUID.randomUUID();

        Account account = new Account(accountId, userId, AccountType.CHECKING, "Conta", BigDecimal.TEN);
        BillingCycle cycle = new BillingCycle(cycleId, cardId, LocalDate.now(), LocalDate.now().plusDays(10), LocalDate.now().plusDays(20), BigDecimal.TEN, BillingCycleStatus.OPEN);
        CreditCard card = new CreditCard(cardId, accountId, userId, "Card", BigDecimal.TEN, 1, 10);

        when(accountRepository.findById(accountId)).thenReturn(account);
        when(billingCycleRepository.findById(cycleId)).thenReturn(cycle);
        when(creditCardRepository.findById(cardId)).thenReturn(card);
        when(categoryRepository.findByNameAndUserId(anyString(), any())).thenReturn(null);
        when(categoryRepository.save(any())).thenReturn(new Category(UUID.randomUUID(), userId, "Fatura Paga", "#9C27B0", "CreditCard", TransactionType.EXPENSE));

        useCase.BillingProcessPayment(userId, cardId, cycleId, accountId, new BigDecimal("5.00"));

        verify(accountRepository).debit(eq(accountId), any(BigDecimal.class));
        verify(billingCycleRepository).save(any(BillingCycle.class));
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Deve falhar se a conta bancária não for encontrada")
    void shouldFailWhenAccountNotFound() {
        when(accountRepository.findById(any())).thenReturn(null);
        assertThrows(BusinessRuleException.class, () ->
                useCase.BillingProcessPayment(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), BigDecimal.TEN));
    }

    @Test
    @DisplayName("Deve falhar se o cartão de crédito não for encontrado")
    void shouldFailWhenCreditCardNotFound() {
        UUID userId = UUID.randomUUID();
        UUID accId = UUID.randomUUID();
        Account account = new Account(accId, userId, AccountType.CHECKING, "Conta", BigDecimal.TEN);

        when(accountRepository.findById(accId)).thenReturn(account);
        when(billingCycleRepository.findById(any())).thenReturn(new BillingCycle(null, UUID.randomUUID(), LocalDate.now(), LocalDate.now().plusDays(1), LocalDate.now().plusDays(2), BigDecimal.TEN, BillingCycleStatus.OPEN));
        when(creditCardRepository.findById(any())).thenReturn(null);

        assertThrows(BusinessRuleException.class, () ->
                useCase.BillingProcessPayment(userId, UUID.randomUUID(), UUID.randomUUID(), accId, BigDecimal.TEN));
    }

    @Test
    @DisplayName("Deve falhar se o cartão de crédito não pertencer ao usuário")
    void shouldFailWhenCardDoesNotBelongToUser() {
        UUID userId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        Account account = new Account(accountId, userId, AccountType.CHECKING, "Conta", BigDecimal.TEN);

        CreditCard wrongUserCard = new CreditCard(cardId, accountId, UUID.randomUUID(), "Card", BigDecimal.TEN, 1, 10);

        when(accountRepository.findById(accountId)).thenReturn(account);
        when(billingCycleRepository.findById(any())).thenReturn(
                new BillingCycle(null, cardId, LocalDate.now(), LocalDate.now().plusDays(1), LocalDate.now().plusDays(2), BigDecimal.TEN, BillingCycleStatus.OPEN)
        );
        when(creditCardRepository.findById(cardId)).thenReturn(wrongUserCard);

        assertThrows(BusinessRuleException.class, () ->
                useCase.BillingProcessPayment(userId, cardId, UUID.randomUUID(), accountId, BigDecimal.TEN));
    }

    @Test
    @DisplayName("Deve criar categoria 'Fatura Paga' caso ela não exista")
    void shouldCreateCategoryWhenNotFound() {
        UUID userId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        UUID cycleId = UUID.randomUUID();
        Account account = new Account(accountId, userId, AccountType.CHECKING, "Conta", BigDecimal.TEN);
        BillingCycle cycle = new BillingCycle(cycleId, cardId, LocalDate.now(), LocalDate.now().plusDays(10), LocalDate.now().plusDays(20), BigDecimal.TEN, BillingCycleStatus.OPEN);
        CreditCard card = new CreditCard(cardId, accountId, userId, "Card", BigDecimal.TEN, 1, 10);

        when(accountRepository.findById(accountId)).thenReturn(account);
        when(billingCycleRepository.findById(cycleId)).thenReturn(cycle);
        when(creditCardRepository.findById(cardId)).thenReturn(card);
        when(categoryRepository.findByNameAndUserId("Fatura Paga", userId)).thenReturn(null);
        when(categoryRepository.save(any())).thenReturn(new Category(UUID.randomUUID(), userId, "Fatura Paga", "#9C27B0", "CreditCard", TransactionType.EXPENSE));

        useCase.BillingProcessPayment(userId, cardId, cycleId, accountId, new BigDecimal("5.00"));

        verify(categoryRepository).save(any(Category.class));
    }
    @Test
    @DisplayName("Deve processar pagamento parcial e marcar parcela como parcial")
    void shouldProcessPartialPayment() {
        UUID userId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        UUID cycleId = UUID.randomUUID();
        UUID installmentId = UUID.randomUUID();

        Account account = new Account(accountId, userId, AccountType.CHECKING, "Conta", BigDecimal.TEN);
        BillingCycle cycle = new BillingCycle(cycleId, cardId, LocalDate.now(), LocalDate.now().plusDays(10), LocalDate.now().plusDays(20), BigDecimal.TEN, BillingCycleStatus.OPEN);
        CreditCard card = new CreditCard(cardId, accountId, userId, "Card", BigDecimal.TEN, 1, 10);
        // Parcela de 100, paga apenas 50
        CreditCardInstallment installment = new CreditCardInstallment(installmentId, UUID.randomUUID(), cycleId, 1, new BigDecimal("100.00"), InstallmentStatus.PENDING);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));
        when(accountRepository.findById(accountId)).thenReturn(account);
        when(billingCycleRepository.findById(cycleId)).thenReturn(cycle);
        when(creditCardRepository.findById(cardId)).thenReturn(card);
        when(installmentRepository.findByBillingCycleId(cycleId)).thenReturn(List.of(installment));
        when(categoryRepository.findByNameAndUserId(anyString(), any())).thenReturn(new Category(UUID.randomUUID(), userId, "Fatura Paga", "#9C27B0", "CreditCard", TransactionType.EXPENSE));

        // Paga 50.00 de uma parcela de 100.00
        useCase.BillingProcessPayment(userId, cardId, cycleId, accountId, new BigDecimal("50.00"));

        // Verifica se a parcela foi marcada como PARTIAL (executa o else do if)
        verify(installmentRepository).save(argThat(inst -> inst.getStatus() == InstallmentStatus.PARTIAL));
        // Verifica se o processamento na meta foi disparado
        verify(processTransactionInGoal).execute(any(Transaction.class));
    }

    @Test
    @DisplayName("Deve marcar parcela como PAGA quando valor é igual ao montante")
    void shouldMarkAsPaidWhenAmountIsEqual() {
        UUID userId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        UUID cycleId = UUID.randomUUID();

        Account account = new Account(accountId, userId, AccountType.CHECKING, "Conta", BigDecimal.TEN);
        BillingCycle cycle = new BillingCycle(cycleId, cardId, LocalDate.now(), LocalDate.now().plusDays(10), LocalDate.now().plusDays(20), BigDecimal.TEN, BillingCycleStatus.OPEN);
        CreditCard card = new CreditCard(cardId, accountId, userId, "Card", BigDecimal.TEN, 1, 10);
        // Parcela de 100, paga 100
        CreditCardInstallment installment = new CreditCardInstallment(UUID.randomUUID(), UUID.randomUUID(), cycleId, 1, new BigDecimal("100.00"), InstallmentStatus.PENDING);

        when(accountRepository.findById(accountId)).thenReturn(account);
        when(billingCycleRepository.findById(cycleId)).thenReturn(cycle);
        when(creditCardRepository.findById(cardId)).thenReturn(card);
        when(installmentRepository.findByBillingCycleId(cycleId)).thenReturn(List.of(installment));
        when(categoryRepository.findByNameAndUserId(anyString(), any())).thenReturn(new Category(UUID.randomUUID(), userId, "Fatura Paga", "#9C27B0", "CreditCard", TransactionType.EXPENSE));

        // Paga 100.00 de uma parcela de 100.00
        useCase.BillingProcessPayment(userId, cardId, cycleId, accountId, new BigDecimal("100.00"));

        // Verifica se a parcela foi marcada como PAID
        verify(installmentRepository).save(argThat(inst -> inst.getStatus() == InstallmentStatus.PAID));
    }
}