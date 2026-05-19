package com.nadson.myfinance.infrastructure.adapter.persistence;

import com.nadson.myfinance.application.port.out.CreditCardInstallmentRepositoryPort;
import com.nadson.myfinance.domain.entity.CreditCardInstallment;
import com.nadson.myfinance.infrastructure.adapter.persistence.entity.CreditCardInstallmentJpaEntity;
import com.nadson.myfinance.infrastructure.adapter.persistence.repository.SpringCreditCardInstallmentRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class CreditCardInstallmentPersistenceAdapter implements CreditCardInstallmentRepositoryPort {

    private final SpringCreditCardInstallmentRepository repository;

    public CreditCardInstallmentPersistenceAdapter(SpringCreditCardInstallmentRepository repository) {
        this.repository = repository;
    }

    @Override
    public CreditCardInstallment save(CreditCardInstallment installment) {
        CreditCardInstallmentJpaEntity entity = repository.findById(installment.getId())
                .orElse(new CreditCardInstallmentJpaEntity(installment));

        entity.setStatus(installment.getStatus());
        entity.setAmount(installment.getAmount());

        return repository.save(entity).toDomain();
    }

    @Override
    public List<CreditCardInstallment> findByBillingCycleId(UUID billingCycleId) {
        return repository.findByBillingCycleId(billingCycleId).stream()
                .map(CreditCardInstallmentJpaEntity::toDomain)
                .toList();
    }

    @Override
    public List<CreditCardInstallment> findByPurchaseId(UUID purchaseId) {
        return repository.findByPurchaseId(purchaseId).stream()
                .map(CreditCardInstallmentJpaEntity::toDomain)
                .toList();
    }
}