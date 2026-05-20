package com.nadson.myfinance.infrastructure.adapter.persistence.entity;

import com.nadson.myfinance.domain.entity.BillingPayment;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "billing_payments")
public class BillingPaymentJpaEntity {
    @Id
    @Column(name = "id")
    private UUID id;

    @Column(nullable = false)
    private UUID billingCycleId;

    @Column(nullable = false)
    private UUID accountId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDateTime paymentDate;

    public BillingPaymentJpaEntity() {
    }

    public BillingPaymentJpaEntity(UUID id, UUID billingCycleId, UUID accountId, BigDecimal amount, LocalDateTime paymentDate) {
        this.id = id;
        this.billingCycleId = billingCycleId;
        this.accountId = accountId;
        this.amount = amount;
        this.paymentDate = paymentDate;
    }

    public static BillingPaymentJpaEntity fromDomain(BillingPayment domain) {
        return new BillingPaymentJpaEntity(
                domain.id(),
                domain.billingCycleId(),
                domain.accountId(),
                domain.amount(),
                domain.paymentDate()
        );
    }

    public BillingPayment toDomain() {
        return new BillingPayment(
                this.id,
                this.billingCycleId,
                this.accountId,
                this.amount,
                this.paymentDate
        );
    }

    public UUID getId() {
        return id;
    }

    public UUID getBillingCycleId() {
        return billingCycleId;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }
}
