package com.nadson.myfinance.infrastructure.adapter.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class CreateAccountRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotBlank(message = "Account type is required")
    private String type;
    
    public CreateAccountRequest() {
    }
    public CreateAccountRequest(String name, UUID userId, String type) {
        this.name = name;
        this.userId = userId;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
