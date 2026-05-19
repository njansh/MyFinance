package com.nadson.myfinance.domain.records;

import com.nadson.myfinance.domain.enums.InstallmentStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record InstallmentDTO(
        UUID id,
        String description,
        int currentInstallment,
        int totalInstallments,
        BigDecimal amount,
        InstallmentStatus status
) {}
