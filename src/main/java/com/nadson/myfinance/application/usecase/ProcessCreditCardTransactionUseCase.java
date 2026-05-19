package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.ProcessCreditCardTransactionPort;
import com.nadson.myfinance.application.port.out.*;
import com.nadson.myfinance.domain.entity.*;
import com.nadson.myfinance.domain.enums.BillingCycleStatus;
import com.nadson.myfinance.domain.enums.InstallmentStatus;
import com.nadson.myfinance.domain.exception.BusinessRuleException;
import com.nadson.myfinance.domain.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.UUID;

public class ProcessCreditCardTransactionUseCase implements ProcessCreditCardTransactionPort {

    private final CreditCardRepositoryPort creditCardRepository;
    private final BillingCycleRepositoryPort billingCycleRepository;
    private final CreditCardPurchaseRepositoryPort purchaseRepository;
    private final CreditCardInstallmentRepositoryPort installmentRepository;

    public ProcessCreditCardTransactionUseCase(
            CreditCardRepositoryPort creditCardRepository,
            BillingCycleRepositoryPort billingCycleRepository,
            CreditCardPurchaseRepositoryPort purchaseRepository,
            CreditCardInstallmentRepositoryPort installmentRepository) {
        this.creditCardRepository = creditCardRepository;
        this.billingCycleRepository = billingCycleRepository;
        this.purchaseRepository = purchaseRepository;
        this.installmentRepository = installmentRepository;
    }

    @Override
    @Transactional
    public void execute(UUID userId, UUID creditCardId,UUID categoryId, String description, BigDecimal totalAmount, LocalDate transactionDate, int installmentsCount) {

        CreditCard card = creditCardRepository.findById(creditCardId);
        if (card == null || !card.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Credit card not found or access denied");
        }

        BigDecimal usedLimit = billingCycleRepository.findUnpaidCyclesByCardId(creditCardId).stream()
                .map(BillingCycle::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (usedLimit.add(totalAmount).compareTo(card.getCreditLimit()) > 0) {
            throw new BusinessRuleException("Insufficient credit limit");
        }

        CreditCardPurchase purchase = new CreditCardPurchase(null, creditCardId, categoryId, description, totalAmount, installmentsCount, transactionDate);
        purchase = purchaseRepository.save(purchase);

        BigDecimal baseInstallmentAmount = totalAmount.divide(BigDecimal.valueOf(installmentsCount), 2, RoundingMode.HALF_EVEN);
        BigDecimal sumDistributed = BigDecimal.ZERO;

        for (int i = 1; i <= installmentsCount; i++) {
            BigDecimal currentInstallmentAmount = baseInstallmentAmount;

            if (i == installmentsCount) {
                currentInstallmentAmount = totalAmount.subtract(sumDistributed);
            } else {
                sumDistributed = sumDistributed.add(currentInstallmentAmount);
            }

            LocalDate installmentDate = transactionDate.plusMonths(i - 1);

            BillingCycle cycle = billingCycleRepository.findOpenCycleByCardId(creditCardId, installmentDate);
            if (cycle == null) {
                cycle = createNextCycle(card, installmentDate);
            }

            CreditCardInstallment installment = new CreditCardInstallment(
                    null, purchase.getId(), cycle.getId(), i, currentInstallmentAmount, InstallmentStatus.PENDING
            );

            cycle.addInstallment(installment);

            cycle = billingCycleRepository.save(cycle);
            installmentRepository.save(installment);
        }
    }

    private BillingCycle createNextCycle(CreditCard card, LocalDate referenceDate) {
        LocalDate closingDate = referenceDate.withDayOfMonth(card.getClosingDay());

        if (referenceDate.isAfter(closingDate)) {
            closingDate = closingDate.plusMonths(1);
        }

        LocalDate startDate = closingDate.minusMonths(1).plusDays(1);

        LocalDate dueDate = closingDate.withDayOfMonth(card.getDueDay());
        if (card.getDueDay() <= card.getClosingDay()) {
            dueDate = dueDate.plusMonths(1);
        }

        return new BillingCycle(null, card.getId(), startDate, closingDate, dueDate, BigDecimal.ZERO, BillingCycleStatus.OPEN);
    }
}
