package com.nadson.myfinance.infrastructure.adapter.persistence.entity;

import com.nadson.myfinance.domain.entity.CreditCard;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "credit_cards")
public class CreditCardJpaEntity {
    @Id
    private UUID id;
    @Column(name = "account_id", nullable = false)
    private UUID accountId;
    @Column(nullable = false)
    private String name;
    @Column(name = "credit_limit", nullable = false, precision = 19, scale = 4)
    private BigDecimal creditLimit;
    @Column(name = "closing_day", nullable = false)
    private int closingDay;
    @Column(name = "due_day", nullable = false)
    private int dueDay;

    public CreditCardJpaEntity() {}

    public CreditCardJpaEntity(CreditCard domain) {
        this.id = domain.getId();
        this.accountId = domain.getAccountId();
        this.name = domain.getName();
        this.creditLimit = domain.getCreditLimit();
        this.closingDay = domain.getClosingDay();
        this.dueDay = domain.getDueDay();
    }

    public CreditCard toDomain() {
        return new CreditCard(id, accountId, name, creditLimit, closingDay, dueDay);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    public int getClosingDay() {
        return closingDay;
    }

    public void setClosingDay(int closingDay) {
        this.closingDay = closingDay;
    }

    public int getDueDay() {
        return dueDay;
    }

    public void setDueDay(int dueDay) {
        this.dueDay = dueDay;
    }
}
