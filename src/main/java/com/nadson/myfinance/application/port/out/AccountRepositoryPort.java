package com.nadson.myfinance.application.port.out;

import com.nadson.myfinance.domain.entity.Account;

import java.util.List;
import java.util.UUID;

public interface AccountRepositoryPort {
     Account save(Account account);

    Account findById(UUID accountId);
    List<Account>findByUserId(UUID userId);
}
