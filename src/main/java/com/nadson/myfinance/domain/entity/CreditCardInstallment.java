package com.nadson.myfinance.domain.entity;

import com.nadson.myfinance.domain.enums.InstallmentStatus;
import com.nadson.myfinance.domain.exception.BusinessRuleException;
import java.math.BigDecimal;
import java.util.UUID;

public class CreditCardInstallment {
    private UUID id;
    private UUID purchaseId;
    private UUID billingCycleId;
    private int installmentNumber;
    private BigDecimal amount;
    private InstallmentStatus status;

    public CreditCardInstallment(UUID id, UUID purchaseId, UUID billingCycleId, int installmentNumber, BigDecimal amount, InstallmentStatus status) {
        this.id = id == null ? UUID.randomUUID() : id;
        this.purchaseId = purchaseId;
        this.billingCycleId = billingCycleId;
        this.installmentNumber = installmentNumber;
        this.amount = amount;
        this.status = status == null ? InstallmentStatus.PENDING : status;
        validate();
    }

    private void validate() {
        if (purchaseId == null) throw new BusinessRuleException("Purchase ID is required");
        if (billingCycleId == null) throw new BusinessRuleException("Billing Cycle ID is required");
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) throw new BusinessRuleException("Amount must be greater than zero");
        if (installmentNumber < 1) throw new BusinessRuleException("Installment number must be at least 1");
    }

    public void markAsPaid() {
        this.status = InstallmentStatus.PAID;
    }


    public UUID getId() { return id; }
    public UUID getPurchaseId() { return purchaseId; }
    public UUID getBillingCycleId() { return billingCycleId; }
    public int getInstallmentNumber() { return installmentNumber; }
    public BigDecimal getAmount() { return amount; }
    public InstallmentStatus getStatus() { return status; }
}