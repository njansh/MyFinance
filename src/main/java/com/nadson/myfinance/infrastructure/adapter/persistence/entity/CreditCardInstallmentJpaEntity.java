package com.nadson.myfinance.infrastructure.adapter.persistence.entity;

import com.nadson.myfinance.domain.entity.CreditCardInstallment;
import com.nadson.myfinance.domain.enums.InstallmentStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "credit_card_installments")
public class CreditCardInstallmentJpaEntity {
    @Id
    private UUID id;

    @Column(name = "purchase_id", nullable = false)
    private UUID purchaseId;

    @Column(name = "billing_cycle_id", nullable = false)
    private UUID billingCycleId;

    @Column(name = "installment_number", nullable = false)
    private int installmentNumber;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InstallmentStatus status;

    public CreditCardInstallmentJpaEntity() {}

    public CreditCardInstallmentJpaEntity(CreditCardInstallment domain) {
        this.id = domain.getId();
        this.purchaseId = domain.getPurchaseId();
        this.billingCycleId = domain.getBillingCycleId();
        this.installmentNumber = domain.getInstallmentNumber();
        this.amount = domain.getAmount();
        this.status = domain.getStatus();
    }

    public CreditCardInstallment toDomain() {
        return new CreditCardInstallment(id, purchaseId, billingCycleId, installmentNumber, amount, status);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(UUID purchaseId) {
        this.purchaseId = purchaseId;
    }

    public UUID getBillingCycleId() {
        return billingCycleId;
    }

    public void setBillingCycleId(UUID billingCycleId) {
        this.billingCycleId = billingCycleId;
    }

    public int getInstallmentNumber() {
        return installmentNumber;
    }

    public void setInstallmentNumber(int installmentNumber) {
        this.installmentNumber = installmentNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public InstallmentStatus getStatus() {
        return status;
    }

    public void setStatus(InstallmentStatus status) {
        this.status = status;
    }
}
