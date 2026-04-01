package com.nadson.myfinance.application.port.in;
import com.nadson.myfinance.domain.entity.Account;

public interface CreateAccountPort {
    Account execute(Account account);
}