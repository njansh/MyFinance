package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.GetBillingCycleByDatePort;
import com.nadson.myfinance.application.port.out.*;
import com.nadson.myfinance.domain.entity.*;
import com.nadson.myfinance.domain.exception.ResourceNotFoundException;
import com.nadson.myfinance.domain.records.BillingCycleDetailsDTO;
import com.nadson.myfinance.domain.records.InstallmentDTO;

import java.util.List;
import java.util.UUID;

public class GetBillingCycleByDateUseCase implements GetBillingCycleByDatePort {

    private final BillingCycleRepositoryPort billingCycleRepository;
    private final CreditCardRepositoryPort creditCardRepository;
    private final CreditCardInstallmentRepositoryPort installmentRepository;
    private final CreditCardPurchaseRepositoryPort purchaseRepository;

    public GetBillingCycleByDateUseCase(
            BillingCycleRepositoryPort billingCycleRepository,
            CreditCardRepositoryPort creditCardRepository,
            CreditCardInstallmentRepositoryPort installmentRepository,
            CreditCardPurchaseRepositoryPort purchaseRepository) {
        this.billingCycleRepository = billingCycleRepository;
        this.creditCardRepository = creditCardRepository;
        this.installmentRepository = installmentRepository;
        this.purchaseRepository = purchaseRepository;
    }

    @Override
    public BillingCycleDetailsDTO execute(UUID userId, UUID creditCardId, int month, int year) {
        CreditCard card = creditCardRepository.findById(creditCardId);
        if (card == null || !card.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Credit card not found or access denied");
        }

        BillingCycle cycle = billingCycleRepository.findByCardIdAndMonthYear(creditCardId, month, year);
        if (cycle == null) {
            throw new ResourceNotFoundException("Billing cycle not found for " + month + "/" + year);
        }

        List<CreditCardInstallment> installments = installmentRepository.findByBillingCycleId(cycle.getId());

        List<InstallmentDTO> items = installments.stream().map(inst -> {
            CreditCardPurchase purchase = purchaseRepository.findById(inst.getPurchaseId());
            return new InstallmentDTO(
                    inst.getId(),
                    purchase != null ? purchase.getDescription() : "Unknown Purchase",
                    inst.getInstallmentNumber(),
                    purchase != null ? purchase.getTotalInstallments() : 0,
                    inst.getAmount(),
                    inst.getStatus()
            );
        }).toList();

        return new BillingCycleDetailsDTO(cycle.getId(), cycle.getDueDate(), cycle.getTotalAmount(), items);
    }
}