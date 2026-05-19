package com.nadson.myfinance.domain.records;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record BillingCycleDetailsDTO(
        UUID id,
        LocalDate dueDate,
        BigDecimal totalAmount,
        List<InstallmentDTO> items
) {}
