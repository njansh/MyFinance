package com.nadson.myfinance.application.port.out;

import com.nadson.myfinance.domain.entity.Account;

public interface AccountRepositoryPort {
    public Account save(Account account);
}
