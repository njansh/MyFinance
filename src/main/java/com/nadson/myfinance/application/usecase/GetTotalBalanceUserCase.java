package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.GetTotalBalancePort;
import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
import com.nadson.myfinance.application.port.out.UserRepositoryPort;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.exception.AccountNotFoundException;
import com.nadson.myfinance.domain.exception.UserNotFoundException;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class GetTotalBalanceUserCase implements GetTotalBalancePort {
    private final AccountRepositoryPort accountRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;

    public GetTotalBalanceUserCase(AccountRepositoryPort accountRepositoryPort, UserRepositoryPort userRepositoryPort) {
        this.accountRepositoryPort = accountRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public BigDecimal execute(UUID userId) {
        if(userRepositoryPort.findById(userId)==null){
throw new UserNotFoundException(userId);
        }
        List<Account>accounts=accountRepositoryPort.findByUserId(userId);

return accounts.stream().map(Account::getBalance).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
