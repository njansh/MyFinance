package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.UpdateAccountPort;
import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.enums.AccountType;

import java.math.BigDecimal;
import java.util.UUID;

public class UpdateAccountUseCase implements UpdateAccountPort {
    private final AccountRepositoryPort accountRepositoryPort;

    public UpdateAccountUseCase(AccountRepositoryPort accountRepositoryPort) {
        this.accountRepositoryPort = accountRepositoryPort;
    }

    @Override
    public Account execute(UUID accountId, UUID userId, String name, BigDecimal balance, String type) {
        Account account = accountRepositoryPort.findById(accountId);
        if (account == null) throw new RuntimeException("Account not found");
        if (!account.getUserId().equals(userId)) {
            throw new SecurityException("User does not have permission to update this account");
        }

        AccountType accountType = (type != null) ? AccountType.valueOf(type.toUpperCase()) : null;

        account.update(name, balance, accountType);
        return accountRepositoryPort.save(account);
    }
}
