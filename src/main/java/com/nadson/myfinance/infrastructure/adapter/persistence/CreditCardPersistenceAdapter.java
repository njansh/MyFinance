package com.nadson.myfinance.infrastructure.adapter.persistence;

import com.nadson.myfinance.application.port.out.CreditCardRepositoryPort;
import com.nadson.myfinance.domain.entity.CreditCard;
import com.nadson.myfinance.infrastructure.adapter.persistence.entity.CategoryJpaEntity;
import com.nadson.myfinance.infrastructure.adapter.persistence.entity.CreditCardJpaEntity;
import com.nadson.myfinance.infrastructure.adapter.persistence.repository.SpringCreditCardRepository;

import java.util.UUID;

public class CreditCardPersistenceAdapter implements CreditCardRepositoryPort {
    private final SpringCreditCardRepository springCreditCardRepository;
    public CreditCardPersistenceAdapter(SpringCreditCardRepository springCreditCardRepository) {
        this.springCreditCardRepository = springCreditCardRepository;
    }
    @Override
    public CreditCard save(CreditCard creditCard) {
        CreditCardJpaEntity creditCardJpaEntity = new CreditCardJpaEntity(creditCard);
        return springCreditCardRepository.save(creditCardJpaEntity).toDomain();
    }

    @Override
    public CreditCard findById(UUID creditCardId) {
        return springCreditCardRepository.findById(creditCardId)
                .map(CreditCardJpaEntity::toDomain)
                .orElse(null);
    }
}
