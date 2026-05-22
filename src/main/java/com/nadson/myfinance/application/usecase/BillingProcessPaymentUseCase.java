package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.BillingProcessPaymentPort;
import com.nadson.myfinance.application.port.out.*;
import com.nadson.myfinance.domain.entity.*;
import com.nadson.myfinance.domain.enums.InstallmentStatus;
import com.nadson.myfinance.domain.enums.TransactionStatus;
import com.nadson.myfinance.domain.enums.TransactionType;
import com.nadson.myfinance.domain.exception.BusinessRuleException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class BillingProcessPaymentUseCase implements BillingProcessPaymentPort {

    private final CreditCardInstallmentRepositoryPort installmentRepository;
    private final BillingPaymentRepositoryPort paymentRepository;
    private final AccountRepositoryPort accountRepository;
    private final BillingCycleRepositoryPort billingCycleRepository;
    private final CreditCardRepositoryPort creditCardRepository;
    private final TransactionRepositoryPort transactionRepository;
    private final CategoryRepositoryPort categoryRepository;

    public BillingProcessPaymentUseCase(CreditCardInstallmentRepositoryPort installmentRepository,
                                        BillingPaymentRepositoryPort paymentRepository,
                                        AccountRepositoryPort accountRepository,
                                        BillingCycleRepositoryPort billingCycleRepository,
                                        CreditCardRepositoryPort creditCardRepository,
                                        TransactionRepositoryPort transactionRepository, CategoryRepositoryPort categoryRepository) {
        this.installmentRepository = installmentRepository;
        this.paymentRepository = paymentRepository;
        this.accountRepository = accountRepository;
        this.billingCycleRepository = billingCycleRepository;
        this.creditCardRepository = creditCardRepository;
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    @Override
    public void BillingProcessPayment(UUID userId, UUID cardId, UUID cycleId, UUID accountId, BigDecimal amountToPay) {
        BillingCycle cycle = billingCycleRepository.findById(cycleId);
        validateOwnership(userId, cardId, cycleId, accountId, cycle);

        accountRepository.debit(accountId, amountToPay);

        cycle.registerPayment(amountToPay);
        billingCycleRepository.save(cycle);

        List<CreditCardInstallment> installments = installmentRepository.findByBillingCycleId(cycleId).stream()
                .filter(i -> i.getStatus() == InstallmentStatus.PENDING || i.getStatus() == InstallmentStatus.PARTIAL)
                .sorted(Comparator.comparing(CreditCardInstallment::getAmount).reversed())
                .toList();

        BigDecimal remaining = amountToPay;

        for (CreditCardInstallment inst : installments) {
            if (remaining.compareTo(BigDecimal.ZERO) <= 0) break;

            BigDecimal paymentForThisOne = remaining.min(inst.getAmount());

            inst.setAmount(inst.getAmount().subtract(paymentForThisOne));

            if (inst.getAmount().compareTo(BigDecimal.ZERO) == 0) {
                inst.setStatus(InstallmentStatus.PAID);
            } else {
                inst.setStatus(InstallmentStatus.PARTIAL);
            }

            installmentRepository.save(inst);
            remaining = remaining.subtract(paymentForThisOne);
        }

        LocalDateTime now = LocalDateTime.now();
        paymentRepository.save(new BillingPayment(UUID.randomUUID(), cycleId, accountId, amountToPay, now));
        Category categoriaFatura = categoryRepository.findByNameAndUserId("Fatura Paga", userId);
        if (categoriaFatura == null) {
            categoriaFatura = categoryRepository.save(new Category(
                    UUID.randomUUID(),
                    userId,
                    "Fatura Paga",
                    "#9C27B0",
                    TransactionType.EXPENSE
            ));
        }
        Transaction transaction = new Transaction(
                UUID.randomUUID(),
                "Pagamento de Fatura",
                amountToPay,
                now,
                TransactionType.EXPENSE,
                accountId,
                categoriaFatura.getCategoryId(),
                true,
                null,
                null,
                TransactionStatus.COMPLETED,
                null
        );
        transactionRepository.save(transaction);
    }

    private void validateOwnership(UUID userId, UUID cardId, UUID cycleId, UUID accountId, BillingCycle cycle) {
        Account account = accountRepository.findById(accountId);
        if (account == null) {
            throw new BusinessRuleException("Bank account not found.");
        }
        if (!account.getUserId().equals(userId)) {
            throw new BusinessRuleException("Bank account does not belong to this user.");
        }

        if (cycle == null) {
            throw new BusinessRuleException("Billing cycle not found.");
        }
        if (!cycle.getCreditCardId().equals(cardId)) {
            throw new BusinessRuleException("The billing cycle does not belong to the provided credit card.");
        }

        CreditCard card = creditCardRepository.findById(cardId);
        if (card == null) {
            throw new BusinessRuleException("Credit card not found.");
        }
        if (!card.getUserId().equals(userId)) {
            throw new BusinessRuleException("The credit card does not belong to this user.");
        }
    }
}
