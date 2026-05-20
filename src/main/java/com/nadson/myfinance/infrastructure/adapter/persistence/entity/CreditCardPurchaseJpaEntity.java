package com.nadson.myfinance.infrastructure.adapter.persistence.entity;

import com.nadson.myfinance.domain.entity.CreditCardPurchase;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "credit_card_purchases")
public class CreditCardPurchaseJpaEntity {
    @Id
    private UUID id;
    
    @Column(name = "credit_card_id", nullable = false)
    private UUID creditCardId;
    @Column(name = "category_id", nullable = false)
    private UUID categoryId;
    
    @Column(nullable = false)
    private String description;
    
    @Column(name = "total_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalAmount;
    
    @Column(name = "total_installments", nullable = false)
    private int totalInstallments;
    
    @Column(name = "purchase_date", nullable = false)
    private LocalDate purchaseDate;

    public CreditCardPurchaseJpaEntity() {}

    public CreditCardPurchaseJpaEntity(CreditCardPurchase domain) {
        this.id = domain.getId();
        this.creditCardId = domain.getCreditCardId();
        this.categoryId = domain.getCategoryId();
        this.description = domain.getDescription();
        this.totalAmount = domain.getTotalAmount();
        this.totalInstallments = domain.getTotalInstallments();
        this.purchaseDate = domain.getPurchaseDate();
    }

    public CreditCardPurchase toDomain() {
        return new CreditCardPurchase(id, creditCardId, categoryId, description, totalAmount, totalInstallments, purchaseDate);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCreditCardId() {
        return creditCardId;
    }

    public void setCreditCardId(UUID creditCardId) {
        this.creditCardId = creditCardId;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getTotalInstallments() {
        return totalInstallments;
    }

    public void setTotalInstallments(int totalInstallments) {
        this.totalInstallments = totalInstallments;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
}
