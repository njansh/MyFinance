package com.nadson.myfinance.infrastructure.adapter.persistence.entity;

import com.nadson.myfinance.domain.entity.Goal;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "goals")
public class GoalJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal targetAmount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal currentAmount;

    public GoalJpaEntity() {}

    public GoalJpaEntity(Goal goal) {
        this.id = goal.getId();
        this.userId = goal.getUserId();
        this.description = goal.getDescription();
        this.targetAmount = goal.getTargetAmount();
        this.currentAmount = goal.getCurrentAmount();
    }

    public Goal toDomain() {
        return new Goal(id, userId, description, targetAmount, currentAmount);
    }
}