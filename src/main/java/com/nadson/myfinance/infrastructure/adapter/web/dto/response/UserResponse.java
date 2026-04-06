package com.nadson.myfinance.infrastructure.adapter.web.dto.response;

import com.nadson.myfinance.domain.entity.User;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email
) {
    public static UserResponse fromDomain(User u){
        return new UserResponse(u.getId(), u.getName(), u.getEmail());
    }
}
