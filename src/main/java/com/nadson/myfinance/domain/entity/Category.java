package com.nadson.myfinance.domain.entity;

import com.nadson.myfinance.domain.enums.TransactionType;

import java.util.UUID;

public class Category {
    private UUID categoryId;
    private UUID userId;
    private String name;
    private String colorHex;
    private String icon;
    private TransactionType type;

    public Category(UUID categoryId, UUID userId, String name, String colorHex, String icon, TransactionType type) {
        validate(name, colorHex);
        this.categoryId = (categoryId == null) ? UUID.randomUUID() : categoryId;
        this.userId = userId;
        this.name = name;
        this.colorHex = colorHex;
        this.icon = (icon == null || icon.trim().isEmpty()) ? "Circle" : icon;
        this.type = type;
    }

    public UUID getUserId() { return userId; }
    public UUID getCategoryId() { return categoryId; }
    public String getName() { return name; }
    public String getColorHex() { return colorHex; }
    public String getIcon() { return icon; }
    public TransactionType getType() { return type; }

    private void validate(String name, String colorHex) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (colorHex == null || !colorHex.matches("#[0-9a-fA-F]{6}")) {
            throw new IllegalArgumentException("Invalid color hex format");
        }
    }
}