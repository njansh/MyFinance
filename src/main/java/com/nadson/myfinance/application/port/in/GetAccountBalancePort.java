package com.nadson.myfinance.application.port.in;

import java.math.BigDecimal;
import java.util.UUID;

public interface GetAccountBalancePort {
    BigDecimal execute(UUID accountId);
}
