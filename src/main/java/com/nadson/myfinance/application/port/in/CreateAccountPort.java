package com.nadson.myfinance.application.port.in;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.enums.AccountType;

import java.util.UUID;

public interface CreateAccountPort {
    Account execute(UUID userId, String name, AccountType type);}