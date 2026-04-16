package com.nadson.myfinance.infrastructure.adapter.persistence.entity;

import com.nadson.myfinance.domain.entity.Category;
import com.nadson.myfinance.domain.enums.TransactionType;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "categories")
public class CategoryJpaEntity {
    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    public CategoryJpaEntity() {
    }

    public CategoryJpaEntity(UUID id, String name, String color, TransactionType type) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.type = type;
    }

    public CategoryJpaEntity(Category category) {
        this.id = category.getCategoryId();
        this.name = category.getName();
        this.color = category.getColorHex();
        this.type = category.getType();
    }

    public Category toDomain() {
        return new Category(this.id, this.name, this.color, this.type);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }
}
