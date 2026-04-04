package com.nadson.myfinance.infrastructure.adapter.persistence;

import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.infrastructure.adapter.persistence.entity.AccountJpaEntity;
import com.nadson.myfinance.infrastructure.adapter.persistence.repository.SpringAccountRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AccountPersistenceAdapter implements AccountRepositoryPort {
    private final SpringAccountRepository springAccountRepository;
    public AccountPersistenceAdapter(SpringAccountRepository springAccountRepository) {
        this.springAccountRepository = springAccountRepository;
    }

    @Override
    public Account save(Account account) {
        AccountJpaEntity entity = AccountJpaEntity.fromDomain(account);
        return springAccountRepository.save(entity).toDomain();
    }

    @Override
    public Account findById(UUID accountId) {
        return springAccountRepository.findById(accountId)
                .map(AccountJpaEntity::toDomain)
                .orElse(null);
    }
}
