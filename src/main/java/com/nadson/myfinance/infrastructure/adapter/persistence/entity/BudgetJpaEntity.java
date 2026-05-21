package com.nadson.myfinance.infrastructure.adapter.persistence.entity;

import com.nadson.myfinance.domain.entity.Budget;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "budgets")
public class BudgetJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private UUID categoryId;

    @Column(nullable = false, name = "budget_month")
    private int month;

    @Column(nullable = false, name = "budget_year")
    private int year;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal limitAmount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal spentAmount;

    // Novas colunas para persistir se o usuário já foi alertado no mês corrente
    @Column(nullable = false, name = "alerted_eighty_percent")
    private boolean alertedEightyPercent;

    @Column(nullable = false, name = "alerted_one_hundred_percent")
    private boolean alertedOneHundredPercent;

    public BudgetJpaEntity() {}

    public BudgetJpaEntity(Budget budget) {
        this.id = budget.getId();
        this.userId = budget.getUserId();
        this.categoryId = budget.getCategoryId();
        this.month = budget.getMonth();
        this.year = budget.getYear();
        this.limitAmount = budget.getLimitAmount();
        this.spentAmount = budget.getSpentAmount();
        this.alertedEightyPercent = budget.isAlertedEightyPercent();
        this.alertedOneHundredPercent = budget.isAlertedOneHundredPercent();
    }

    public Budget toDomain() {
        return new Budget(
                this.id,
                this.userId,
                this.categoryId,
                this.month,
                this.year,
                this.limitAmount,
                this.spentAmount,
                this.alertedEightyPercent,
                this.alertedOneHundredPercent
        );
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public UUID getCategoryId() { return categoryId; }
    public void setCategoryId(UUID categoryId) { this.categoryId = categoryId; }

    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public BigDecimal getLimitAmount() { return limitAmount; }
    public void setLimitAmount(BigDecimal limitAmount) { this.limitAmount = limitAmount; }

    public BigDecimal getSpentAmount() { return spentAmount; }
    public void setSpentAmount(BigDecimal spentAmount) { this.spentAmount = spentAmount; }

    public boolean isAlertedEightyPercent() { return alertedEightyPercent; }
    public void setAlertedEightyPercent(boolean alertedEightyPercent) { this.alertedEightyPercent = alertedEightyPercent; }

    public boolean isAlertedOneHundredPercent() { return alertedOneHundredPercent; }
    public void setAlertedOneHundredPercent(boolean alertedOneHundredPercent) { this.alertedOneHundredPercent = alertedOneHundredPercent; }
}
