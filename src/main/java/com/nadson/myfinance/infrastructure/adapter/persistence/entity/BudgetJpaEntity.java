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

    public BudgetJpaEntity() {}

    public BudgetJpaEntity(Budget budget) {
        this.id = budget.getId();
        this.userId = budget.getUserId();
        this.categoryId = budget.getCategoryId();
        this.month = budget.getMonth();
        this.year = budget.getYear();
        this.limitAmount = budget.getLimitAmount();
        this.spentAmount = budget.getSpentAmount();
    }

    public Budget toDomain() {
        Budget budget = new Budget(id, userId, categoryId, month, year, limitAmount);
        budget.addExpense(this.spentAmount); // Restaura o valor já gasto
        return budget;
    }
}