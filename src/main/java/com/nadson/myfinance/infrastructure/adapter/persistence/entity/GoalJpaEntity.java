package com.nadson.myfinance.infrastructure.adapter.persistence.entity;

import com.nadson.myfinance.domain.entity.Goal;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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

    @ElementCollection
    @CollectionTable(name = "goal_accounts", joinColumns = @JoinColumn(name = "goal_id"))
    @Column(name = "account_id")
    private List<UUID> accountIds = new ArrayList<>();

    public GoalJpaEntity() {}

    public GoalJpaEntity(Goal goal) {
        this.id = goal.getId();
        this.userId = goal.getUserId();
        this.description = goal.getDescription();
        this.targetAmount = goal.getTargetAmount();
        this.currentAmount = goal.getCurrentAmount();
        this.accountIds = goal.getAccountIds();
    }

    public Goal toDomain() {
        return new Goal(id, userId, description, targetAmount, currentAmount, accountIds);
    }
}