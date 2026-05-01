package com.nadson.myfinance.domain.entity;

import com.nadson.myfinance.domain.enums.BillingCycleStatus;
import com.nadson.myfinance.domain.exception.BusinessRuleException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class BillingCycle {
    private UUID id;
    private UUID creditCardId;
    private LocalDate startDate;
    private LocalDate closingDate;
    private LocalDate dueDate;
    private BigDecimal totalAmount;
    private BillingCycleStatus status;

    public BillingCycle(UUID id, UUID creditCardId, LocalDate startDate, LocalDate closingDate, LocalDate dueDate, BigDecimal totalAmount, BillingCycleStatus status) {
        this.id = id == null ? UUID.randomUUID() : id;
        this.creditCardId = creditCardId;
        this.startDate = startDate;
        this.closingDate = closingDate;
        this.dueDate = dueDate;
        this.totalAmount = totalAmount == null ? BigDecimal.ZERO : totalAmount;
        this.status = status == null ? BillingCycleStatus.OPEN : status;
        validate();
    }

    private void validate() {
        if (creditCardId == null) throw new BusinessRuleException("Credit card ID is required");
        if (startDate == null) throw new BusinessRuleException("Start date is required");
        if (closingDate == null) throw new BusinessRuleException("Closing date is required");
        if (dueDate == null) throw new BusinessRuleException("Due date is required");
        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) < 0)
            throw new BusinessRuleException("Total amount cannot be negative");
        if (status == null) throw new BusinessRuleException("Status is required");
        
        if (closingDate.isBefore(startDate)) 
            throw new BusinessRuleException("Closing date cannot be before start date");
        if (dueDate.isBefore(closingDate))
            throw new BusinessRuleException("Due date cannot be before closing date");
    }public void addCharge(BigDecimal amount) {
        if (this.status!= BillingCycleStatus.OPEN) {
            throw new BusinessRuleException("Cannot add charges to a closed or paid billing cycle.");
        }
        this.totalAmount = this.totalAmount.add(amount);
    }

    public void closeCycle() {
        this.status = BillingCycleStatus.CLOSED;
    }

    public void markAsPaid() {
        this.status = BillingCycleStatus.PAID;
    }

    public UUID getId() { return id; }
    public UUID getCreditCardId() { return creditCardId; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getClosingDate() { return closingDate; }
    public LocalDate getDueDate() { return dueDate; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public BillingCycleStatus getStatus() { return status; }
}
}
