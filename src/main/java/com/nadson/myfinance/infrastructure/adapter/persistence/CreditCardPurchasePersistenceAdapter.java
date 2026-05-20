package com.nadson.myfinance.infrastructure.adapter.persistence;

import com.nadson.myfinance.application.port.out.CreditCardPurchaseRepositoryPort;
import com.nadson.myfinance.domain.entity.CreditCardPurchase;
import com.nadson.myfinance.infrastructure.adapter.persistence.entity.CreditCardPurchaseJpaEntity;
import com.nadson.myfinance.infrastructure.adapter.persistence.repository.SpringCreditCardPurchaseRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class CreditCardPurchasePersistenceAdapter implements CreditCardPurchaseRepositoryPort {

    private final SpringCreditCardPurchaseRepository repository;

    public CreditCardPurchasePersistenceAdapter(SpringCreditCardPurchaseRepository repository) {
        this.repository = repository;
    }

    @Override
    public CreditCardPurchase save(CreditCardPurchase purchase) {
        CreditCardPurchaseJpaEntity entity = new CreditCardPurchaseJpaEntity(purchase);
        return repository.save(entity).toDomain();
    }

    @Override
    public CreditCardPurchase findById(UUID id) {
        return repository.findById(id)
                .map(CreditCardPurchaseJpaEntity::toDomain)
                .orElse(null);
    }

    @Override
    public List<CreditCardPurchase> findByCreditCardId(UUID creditCardId) {
        return repository.findByCreditCardId(creditCardId).stream()
                .map(CreditCardPurchaseJpaEntity::toDomain)
                .toList();
    }
}