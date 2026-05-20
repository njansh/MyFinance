package com.nadson.myfinance.infrastructure.adapter.persistence.entity;

import com.nadson.myfinance.domain.entity.BillingCycle;
import com.nadson.myfinance.domain.enums.BillingCycleStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "billing_cycles")
public class BillingCycleJpaEntity {
    @Id
    private UUID id;
    @Column(name = "credit_card_id", nullable = false)
    private UUID creditCardId;
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    @Column(name = "closing_date", nullable = false)
    private LocalDate closingDate;
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;
    @Column(name = "total_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalAmount;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BillingCycleStatus status;
    @Version
    private Long version;

    public BillingCycleJpaEntity() {}

    public BillingCycleJpaEntity(BillingCycle domain) {
        this.id = domain.getId();
        this.creditCardId = domain.getCreditCardId();
        this.startDate = domain.getStartDate();
        this.closingDate = domain.getClosingDate();
        this.dueDate = domain.getDueDate();
        this.totalAmount = domain.getTotalAmount();
        this.status = domain.getStatus();
    }

    public BillingCycle toDomain() {
        return new BillingCycle(id, creditCardId, startDate, closingDate, dueDate, totalAmount, status);
    }

    public UUID getId() { return id; }
    public UUID getCreditCardId() { return creditCardId; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getClosingDate() { return closingDate; }
    public LocalDate getDueDate() { return dueDate; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public BillingCycleStatus getStatus() { return status; }
    public Long getVersion() { return version; }

    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public void setStatus(BillingCycleStatus status) { this.status = status; }
}
