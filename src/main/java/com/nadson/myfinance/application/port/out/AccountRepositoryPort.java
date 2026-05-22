package com.nadson.myfinance.application.port.out;

import com.nadson.myfinance.domain.entity.Account;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface AccountRepositoryPort {
     Account save(Account account);

    Account findById(UUID accountId);
    List<Account>findByUserId(UUID userId);

    void updateBalanceAtomic(UUID accountId, BigDecimal amount);
    UUID findUserIdByAccountId(UUID accountId);
    void deleteById(UUID accountId);
    void debit(UUID accountId, BigDecimal amount);
}
