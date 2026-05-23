package com.nadson.myfinance.infrastructure.adapter.persistence;

import com.nadson.myfinance.application.port.out.CreditCardRepositoryPort;
import com.nadson.myfinance.domain.entity.CreditCard;
import com.nadson.myfinance.infrastructure.adapter.persistence.entity.CreditCardJpaEntity;
import com.nadson.myfinance.infrastructure.adapter.persistence.repository.SpringBillingCycleRepository;
import com.nadson.myfinance.infrastructure.adapter.persistence.repository.SpringCreditCardRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
@Component
public class CreditCardPersistenceAdapter implements CreditCardRepositoryPort {
    private final SpringCreditCardRepository springCreditCardRepository;
    private final SpringBillingCycleRepository springBillingCycleRepository;

    public CreditCardPersistenceAdapter(SpringCreditCardRepository springCreditCardRepository, SpringBillingCycleRepository springBillingCycleRepository) {
        this.springCreditCardRepository = springCreditCardRepository;
        this.springBillingCycleRepository = springBillingCycleRepository;
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

    @Override
    public List<CreditCard> findByUserId(UUID userId) {
        return springCreditCardRepository.findByUserId(userId)
                .stream()
                .map(CreditCardJpaEntity::toDomain)
                .toList();
    }

    @Override
    public void deleteAllByAccountId(UUID accountId) {
        springBillingCycleRepository.deleteAllByAccountId(accountId);
        springCreditCardRepository.deleteAllByAccountId(accountId);
    }
}
