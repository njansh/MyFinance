package com.nadson.myfinance.application.port.in;

import com.nadson.myfinance.infrastructure.adapter.web.dto.response.BalanceResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public interface GetAccountBalancePort {
    BalanceResponse execute(UUID accountId, LocalDateTime startDate, LocalDateTime endDate);}
