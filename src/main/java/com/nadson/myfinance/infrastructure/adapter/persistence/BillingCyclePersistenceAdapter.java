package com.nadson.myfinance.infrastructure.adapter.persistence;

import com.nadson.myfinance.application.port.out.BillingCycleRepositoryPort;
import com.nadson.myfinance.domain.entity.BillingCycle;
import com.nadson.myfinance.infrastructure.adapter.persistence.entity.BillingCycleJpaEntity;
import com.nadson.myfinance.infrastructure.adapter.persistence.repository.SpringBillingCycleRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
@Component
public class BillingCyclePersistenceAdapter implements BillingCycleRepositoryPort {
    private final SpringBillingCycleRepository springBillingCycleRepository;

    public BillingCyclePersistenceAdapter(SpringBillingCycleRepository springBillingCycleRepository) {
        this.springBillingCycleRepository = springBillingCycleRepository;
    }

    @Override
    public BillingCycle save(BillingCycle billingCycle) {
        BillingCycleJpaEntity billingCycleJpaEntity =new BillingCycleJpaEntity(billingCycle);
        return springBillingCycleRepository.save(billingCycleJpaEntity).toDomain();
    }

    @Override
    public BillingCycle findOpenCycleByCardId(UUID creditCardId, LocalDate installmentDate) {
        return springBillingCycleRepository.findById(creditCardId)
                .map(BillingCycleJpaEntity::toDomain)
                .orElse(null);
    }

    @Override
    public List<BillingCycle> findUnpaidCyclesByCardId(UUID creditCardId) {
        return springBillingCycleRepository.findAll().stream()
                .map(BillingCycleJpaEntity::toDomain)
                .toList();
    }
    @Override
    public BillingCycle findById(UUID billingCycleId) {
        return springBillingCycleRepository.findById(billingCycleId)
                .map(BillingCycleJpaEntity::toDomain)
                .orElse(null);
    }
}
