package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.GetTotalBalancePort;
import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
import com.nadson.myfinance.domain.entity.Account;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class GetTotalBalanceUserCase implements GetTotalBalancePort {
    private final AccountRepositoryPort accountRepositoryPort;

    public GetTotalBalanceUserCase(AccountRepositoryPort accountRepositoryPort) {
        this.accountRepositoryPort = accountRepositoryPort;
    }

    @Override
    public BigDecimal execute(UUID userId) {
        List<Account>accounts=accountRepositoryPort.findByUserId(userId);

return accounts.stream().map(Account::getBalance).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
