package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.GetAccountport;
import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
import com.nadson.myfinance.domain.entity.Account;

import java.util.UUID;

public class GetAccountUseCase implements GetAccountport {
    private final AccountRepositoryPort accountRepositoryPort;
    public GetAccountUseCase(AccountRepositoryPort accountRepositoryPort) {
        this.accountRepositoryPort = accountRepositoryPort;
    }
    @Override
    public Account execute(UUID accountId) {
        Account account = accountRepositoryPort.findById(accountId);
        if (account == null) {
            throw new IllegalArgumentException("Account not found");
        }
        return account;
    }
}
