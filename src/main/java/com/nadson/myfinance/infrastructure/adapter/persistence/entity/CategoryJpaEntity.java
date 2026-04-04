package com.nadson.myfinance.infrastructure.adapter.persistence.entity;

import com.nadson.myfinance.domain.entity.Category;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
@Entity
@Table(name = "categories")
public class CategoryJpaEntity {
    @Id
    private java.util.UUID id;
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String color;

    public CategoryJpaEntity() {
    }

    public CategoryJpaEntity(java.util.UUID id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }
public CategoryJpaEntity(Category category) {
        this.id = category.getCategoryId();
        this.name = category.getName();
        this.color = category.getColorHex();

}
    public Category toDomain(){
            return new Category(this.id, this.name, this.color);
    }


    public java.util.UUID getId() {
        return id;
    }

    public void setId(java.util.UUID id) {
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


}
