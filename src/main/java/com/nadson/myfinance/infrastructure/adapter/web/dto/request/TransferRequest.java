package com.nadson.myfinance.infrastructure.adapter.web.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransferRequest(UUID fromId, UUID toId, BigDecimal amount, LocalDateTime date) {}