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
    public void execute(UUID creditCardId, BigDecimal amount, LocalDate transactionDate) {
        CreditCard card = creditCardRepository.findById(creditCardId);
        if (card == null) throw new ResourceNotFoundException("Cartão de crédito não encontrado");

        List<BillingCycle> unpaidCycles = billingCycleRepository.findUnpaidCyclesByCardId(creditCardId);
        BigDecimal usedLimit = unpaidCycles.stream()
                .map(BillingCycle::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (usedLimit.add(amount).compareTo(card.getCreditLimit()) > 0) {
            throw new BusinessRuleException("Transação recusada: Excede o limite disponível do cartão.");
        }

        BillingCycle currentCycle = billingCycleRepository.findOpenCycleByCardId(creditCardId);

        if (currentCycle == null) {
            currentCycle = createNextCycle(card, transactionDate);
        } else if (transactionDate.isAfter(currentCycle.getClosingDate())) {
            currentCycle.closeCycle();
            billingCycleRepository.save(currentCycle);
            currentCycle = createNextCycle(card, transactionDate);
        }

        currentCycle.addCharge(amount);
        billingCycleRepository.save(currentCycle);
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