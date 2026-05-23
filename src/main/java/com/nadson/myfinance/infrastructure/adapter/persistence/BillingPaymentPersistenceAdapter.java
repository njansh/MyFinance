package com.nadson.myfinance.infrastructure.adapter.persistence;

import com.nadson.myfinance.application.port.out.BillingPaymentRepositoryPort;
import com.nadson.myfinance.domain.entity.BillingPayment;
import com.nadson.myfinance.infrastructure.adapter.persistence.entity.BillingPaymentJpaEntity;
import com.nadson.myfinance.infrastructure.adapter.persistence.repository.SpringBillingPaymentRepository;
import org.springframework.stereotype.Component;

@Component
public class BillingPaymentPersistenceAdapter implements BillingPaymentRepositoryPort {
    private final SpringBillingPaymentRepository repository;

    public BillingPaymentPersistenceAdapter(SpringBillingPaymentRepository repository) {
        this.repository = repository;
    }

    @Override
    public BillingPayment save(BillingPayment payment) {
        BillingPaymentJpaEntity entity = BillingPaymentJpaEntity.fromDomain(payment);
        return repository.save(entity).toDomain();
    }
}