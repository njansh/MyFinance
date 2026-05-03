package com.nadson.myfinance.infrastructure.adapter.persistence.entity;

import com.nadson.myfinance.domain.entity.RecurringTemplate;
import com.nadson.myfinance.domain.enums.TransactionType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "recurring_templates")
public class RecurringTemplateJpaEntity {
    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private UUID accountId;

    private UUID categoryId;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal expectedAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(nullable = false)
    private int frequencyDay;

    @Column(nullable = false)
    private boolean active;

    private Integer lastExecutedMonth;
    private Integer lastExecutedYear;



    public RecurringTemplateJpaEntity(UUID id, UUID userId, UUID accountId, UUID categoryId, String description,
                                     BigDecimal expectedAmount, TransactionType type, int frequencyDay,
                                     boolean active, Integer lastExecutedMonth, Integer lastExecutedYear) {
        this.id = id;
        this.userId = userId;
        this.accountId = accountId;
        this.categoryId = categoryId;
        this.description = description;
        this.expectedAmount = expectedAmount;
        this.type = type;
        this.frequencyDay = frequencyDay;
        this.active = active;
        this.lastExecutedMonth = lastExecutedMonth;
        this.lastExecutedYear = lastExecutedYear;
    }

    public static RecurringTemplateJpaEntity fromDomain(RecurringTemplate template) {
        return new RecurringTemplateJpaEntity(
                template.getId(),
                template.getUserId(),
                template.getAccountId(),
                template.getCategoryId(),
                template.getDescription(),
                template.getExpectedAmount(),
                template.getType(),
                template.getFrequencyDay(),
                template.isActive(),
                template.getLastExecutedMonth(),
                template.getLastExecutedYear()
        );
    }

    public RecurringTemplate toDomain() {
        RecurringTemplate template = new RecurringTemplate(
                id, userId, accountId, categoryId, description, expectedAmount, type, frequencyDay, active
        );
        if (lastExecutedMonth != null && lastExecutedYear != null) {
            template.setLastExecution(lastExecutedMonth, lastExecutedYear);
        }
        return template;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public UUID getAccountId() { return accountId; }
    public void setAccountId(UUID accountId) { this.accountId = accountId; }

    public UUID getCategoryId() { return categoryId; }
    public void setCategoryId(UUID categoryId) { this.categoryId = categoryId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getExpectedAmount() { return expectedAmount; }
    public void setExpectedAmount(BigDecimal expectedAmount) { this.expectedAmount = expectedAmount; }

    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }

    public int getFrequencyDay() { return frequencyDay; }
    public void setFrequencyDay(int frequencyDay) { this.frequencyDay = frequencyDay; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Integer getLastExecutedMonth() { return lastExecutedMonth; }
    public void setLastExecutedMonth(Integer lastExecutedMonth) { this.lastExecutedMonth = lastExecutedMonth; }

    public Integer getLastExecutedYear() { return lastExecutedYear; }
    public void setLastExecutedYear(Integer lastExecutedYear) { this.lastExecutedYear = lastExecutedYear; }
}
