package com.nadson.myfinance.application.port.in;

import com.nadson.myfinance.domain.entity.Account;

import java.util.UUID;

public interface GetAccountport {
    Account execute(UUID accountId);
}
