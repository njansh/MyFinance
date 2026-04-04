package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.GetAccountBalancePort;
import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
import com.nadson.myfinance.domain.entity.Account;

import java.math.BigDecimal;
import java.util.UUID;

public class GetAccountBalanceUseCase  implements GetAccountBalancePort {
    private final AccountRepositoryPort accountRepositoryPort;
    public GetAccountBalanceUseCase(AccountRepositoryPort accountRepositoryPort) {
        this.accountRepositoryPort = accountRepositoryPort;
    }
    @Override
    public BigDecimal execute(UUID accountId) {
        Account account = accountRepositoryPort.findById(accountId);
        if (account == null) {
            throw new IllegalArgumentException("Account not found");
        }
        return account.getBalance();

    }
}
