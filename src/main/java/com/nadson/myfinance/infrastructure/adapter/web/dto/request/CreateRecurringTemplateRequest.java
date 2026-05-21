package com.nadson.myfinance.infrastructure.adapter.web.dto.request;

import com.nadson.myfinance.domain.enums.TransactionType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateRecurringTemplateRequest(
        @NotBlank(message = "A descrição é obrigatória")
        String description,

        @NotNull(message = "O valor esperado é obrigatório")
        BigDecimal expectedAmount,

        @NotNull(message = "O tipo da transação é obrigatório")
        TransactionType type,

        @NotNull(message = "A conta é obrigatória")
        UUID accountId,

        UUID categoryId,

        @Min(value = 1, message = "O dia de vencimento deve ser no mínimo 1")
        @Max(value = 31, message = "O dia de vencimento deve ser no máximo 31")
        int frequencyDay
) {
}
