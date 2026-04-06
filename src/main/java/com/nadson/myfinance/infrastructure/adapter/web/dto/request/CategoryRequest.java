package com.nadson.myfinance.infrastructure.adapter.web.dto.request;

import com.nadson.myfinance.domain.entity.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

public record CategoryRequest(
        @NotBlank String name,
        @Pattern(regexp = "#[0-9a-fA-F]{6}") String colorHex
) {}

