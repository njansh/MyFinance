package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.CreateCreditCardPort;
import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
import com.nadson.myfinance.application.port.out.CreditCardRepositoryPort;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.entity.CreditCard;
import com.nadson.myfinance.domain.exception.AccountNotFoundException;
import com.nadson.myfinance.domain.exception.BusinessRuleException;

import java.math.BigDecimal;
import java.util.UUID;

public class CreateCreditCardUseCase implements CreateCreditCardPort {
    private final CreditCardRepositoryPort repository;
    private final AccountRepositoryPort accountRepository;

    public CreateCreditCardUseCase(CreditCardRepositoryPort repository, AccountRepositoryPort accountRepository) {
        this.repository = repository;
        this.accountRepository = accountRepository;
    }

    @Override
    public CreditCard execute(UUID userId, String name, BigDecimal creditLimit, int closingDay, int dueDay, UUID accountId) {
        Account account = accountRepository.findById(accountId);
        if (account == null) {
            throw new AccountNotFoundException(accountId);
        }

        if (!account.getUserId().equals(userId)) {
            throw new BusinessRuleException("The provided account does not belong to this user.");
        }

        CreditCard card = new CreditCard(
                UUID.randomUUID(),
                accountId,
                userId,
                name,
                creditLimit,
                closingDay,
                dueDay
        );

        return repository.save(card);
    }
}
