package com.nadson.myfinance.application.port.in;

import com.nadson.myfinance.domain.entity.Account;

import java.util.List;
import java.util.UUID;

public interface ListAccountsByUserPort {
    List<Account> execute(UUID userid);
}
