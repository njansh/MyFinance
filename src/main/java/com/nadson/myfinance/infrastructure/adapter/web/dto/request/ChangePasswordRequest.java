package com.nadson.myfinance.infrastructure.adapter.web.dto.request;

public record ChangePasswordRequest(
        String oldPassword,
        String newPassword
) {}
