package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.ProcessCreditCardTransactionPort;
import com.nadson.myfinance.application.port.out.BillingCycleRepositoryPort;
import com.nadson.myfinance.application.port.out.CreditCardRepositoryPort;
import com.nadson.myfinance.domain.entity.BillingCycle;
import com.nadson.myfinance.domain.entity.CreditCard;
import com.nadson.myfinance.domain.enums.BillingCycleStatus;
import com.nadson.myfinance.domain.exception.BusinessRuleException;
import com.nadson.myfinance.domain.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class ProcessCreditCardTransactionUseCase implements ProcessCreditCardTransactionPort {

    private final CreditCardRepositoryPort creditCardRepository;
    private final BillingCycleRepositoryPort billingCycleRepository;

    public ProcessCreditCardTransactionUseCase(CreditCardRepositoryPort creditCardRepository, BillingCycleRepositoryPort billingCycleRepository) {
        this.creditCardRepository = creditCardRepository;
        this.billingCycleRepository = billingCycleRepository;
    }

    @Override
    @Transactional
    public void execute(UUID creditCardId, BigDecimal totalAmount, LocalDate transactionDate, int installmentsCount) {
        CreditCard card = creditCardRepository.findById(creditCardId);
        if (card == null) throw new ResourceNotFoundException("Cartão não encontrado");

        // 1. Validar se o valor total cabe no limite
        BigDecimal usedLimit = billingCycleRepository.findUnpaidCyclesByCardId(creditCardId).stream()
                .map(BillingCycle::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        if (usedLimit.add(totalAmount).compareTo(card.getCreditLimit()) > 0) {
            throw new BusinessRuleException("Limite insuficiente");
        }

        // 2. Lógica de Parcelamento
        BigDecimal installmentAmount = totalAmount.divide(BigDecimal.valueOf(installmentsCount), 2, RoundingMode.HALF_EVEN);

        for (int i = 0; i < installmentsCount; i++) {
            LocalDate installmentDate = transactionDate.plusMonths(i);

            // Se for a última parcela, ajusta a diferença de centavos (residual)
            if (i == installmentsCount - 1) {
                BigDecimal totalDistributed = installmentAmount.multiply(BigDecimal.valueOf(installmentsCount - 1));
                installmentAmount = totalAmount.subtract(totalDistributed);
            }

            BillingCycle cycle = billingCycleRepository.findOpenCycleByCardId(creditCardId, installmentDate);
            if (cycle == null) {
                cycle = createNextCycle(card, installmentDate);
            }

            cycle.addCharge(installmentAmount);
            billingCycleRepository.save(cycle);
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