package com.nadson.myfinance.domain.entity;

import com.nadson.myfinance.domain.exception.BusinessRuleException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class CreditCardPurchase {
    private UUID id;
    private UUID creditCardId;
    private UUID categoryId;
    private String description;
    private BigDecimal totalAmount;
    private int totalInstallments;
    private LocalDate purchaseDate;

    public CreditCardPurchase(UUID id, UUID creditCardId, UUID categoryId, String description, BigDecimal totalAmount, int totalInstallments, LocalDate purchaseDate) {
        this.id = id == null ? UUID.randomUUID() : id;
        this.creditCardId = creditCardId;
        this.categoryId = categoryId;
        this.description = description;
        this.totalAmount = totalAmount;
        this.totalInstallments = totalInstallments;
        this.purchaseDate = purchaseDate;
        validate();
    }

    private void validate() {
        if (creditCardId == null) throw new BusinessRuleException("Credit card ID is required");
        if (categoryId == null) throw new BusinessRuleException("Category ID is required");
        if (description == null || description.isBlank()) throw new BusinessRuleException("Description is required");
        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) <= 0) throw new BusinessRuleException("Total amount must be greater than zero");
        if (totalInstallments < 1) throw new BusinessRuleException("Installments must be at least 1");
        if (purchaseDate == null) throw new BusinessRuleException("Purchase date is required");
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getCreditCardId() { return creditCardId; }
    public UUID getCategoryId() { return categoryId; }
    public String getDescription() { return description; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public int getTotalInstallments() { return totalInstallments; }
    public LocalDate getPurchaseDate() { return purchaseDate; }
}