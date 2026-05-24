package com.nadson.myfinance.application.port.in;

import com.nadson.myfinance.domain.entity.Account;

import java.math.BigDecimal;
import java.util.UUID;

public interface UpdateAccountPort {
    Account execute(UUID accountId, UUID userId, String name, BigDecimal balance, String type);
}
