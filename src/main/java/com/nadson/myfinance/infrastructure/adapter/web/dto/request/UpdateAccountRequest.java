package com.nadson.myfinance.infrastructure.adapter.web.dto.request;

import java.math.BigDecimal;

public record UpdateAccountRequest(
    String name,
    BigDecimal balance,
    String type
) {}
