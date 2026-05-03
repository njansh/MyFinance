package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.CreateCreditCardPort;
import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
import com.nadson.myfinance.application.port.out.CreditCardRepositoryPort;
import com.nadson.myfinance.domain.entity.CreditCard;
import com.nadson.myfinance.domain.exception.AccountNotFoundException;

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
    public CreditCard execute(String name, BigDecimal creditLimit, int closingDay, int dueDay, UUID accountId) {
        if (accountRepository.findById(accountId) == null) {
            throw new AccountNotFoundException(accountId);
        }
        CreditCard card = new CreditCard(UUID.randomUUID(), accountId, name, creditLimit, closingDay, dueDay);
        return repository.save(card);
    }
}