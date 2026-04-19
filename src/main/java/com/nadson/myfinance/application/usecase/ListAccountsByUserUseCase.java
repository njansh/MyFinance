package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.ListAccountsByUserPort;
import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
import com.nadson.myfinance.application.port.out.UserRepositoryPort;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.exception.UserNotFoundException;

import java.util.List;
import java.util.UUID;

public class ListAccountsByUserUseCase implements ListAccountsByUserPort {
    private final AccountRepositoryPort accountRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;

    public ListAccountsByUserUseCase(AccountRepositoryPort accountRepositoryPort, UserRepositoryPort userRepositoryPort) {
        this.accountRepositoryPort = accountRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
    }


    @Override
    public List<Account> execute(UUID userId) {

        if (userRepositoryPort.findById(userId) == null) {
            throw new UserNotFoundException(userId);
        }

        return accountRepositoryPort.findByUserId(userId);
    }
}
