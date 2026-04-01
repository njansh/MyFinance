package com.nadson.myfinance.application.port.out;

import com.nadson.myfinance.domain.entity.Account;

import java.util.UUID;

public interface AccountRepositoryPort {
     Account save(Account account);

    Account findById(UUID accountId);
}
