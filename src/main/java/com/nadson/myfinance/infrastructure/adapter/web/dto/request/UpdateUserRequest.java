package com.nadson.myfinance.infrastructure.adapter.web.dto.request;

public record UpdateUserRequest(
        String name,
        String email
) {}