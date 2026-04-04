package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.CreateAccountPort;
import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.enums.AccountType;

import java.util.UUID;

public class CreateAccountUseCase implements CreateAccountPort { // Implementando a porta!

    private final AccountRepositoryPort accountRepositoryPort;

    public CreateAccountUseCase(AccountRepositoryPort accountRepositoryPort) {
        this.accountRepositoryPort = accountRepositoryPort;
    }

    @Override
    public Account execute(UUID userId, String name, AccountType type) {
        Account account = new Account( userId, name, type)
        ;

        return accountRepositoryPort.save(account);
    }
}