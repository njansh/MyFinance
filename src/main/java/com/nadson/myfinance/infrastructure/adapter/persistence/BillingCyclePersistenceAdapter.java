package com.nadson.myfinance.infrastructure.adapter.persistence;

import com.nadson.myfinance.application.port.out.BillingCycleRepositoryPort;
import com.nadson.myfinance.domain.entity.BillingCycle;
import com.nadson.myfinance.domain.enums.BillingCycleStatus;
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
        BillingCycleJpaEntity entity = springBillingCycleRepository.findById(billingCycle.getId())
                .orElse(new BillingCycleJpaEntity(billingCycle));

        entity.setTotalAmount(billingCycle.getTotalAmount());
        entity.setStatus(billingCycle.getStatus());

        return springBillingCycleRepository.save(entity).toDomain();
    }

    @Override
    public BillingCycle findOpenCycleByCardId(UUID creditCardId, LocalDate installmentDate) {
        return springBillingCycleRepository.findByCreditCardIdAndStatus(creditCardId, BillingCycleStatus.OPEN)
                .stream()
                .filter(cycle -> !installmentDate.isBefore(cycle.getStartDate()) && !installmentDate.isAfter(cycle.getClosingDate()))
                .findFirst()
                .map(BillingCycleJpaEntity::toDomain)
                .orElse(null);
    }

    @Override
    public List<BillingCycle> findUnpaidCyclesByCardId(UUID creditCardId) {
        return springBillingCycleRepository.findByCreditCardId(creditCardId).stream()
                .filter(cycle -> cycle.getStatus() != BillingCycleStatus.PAID)
                .map(BillingCycleJpaEntity::toDomain)
                .toList();
    }

    @Override
    public BillingCycle findById(UUID billingCycleId) {
        return springBillingCycleRepository.findById(billingCycleId)
                .map(BillingCycleJpaEntity::toDomain)
                .orElse(null);
    }
    @Override
    public BillingCycle findByCardIdAndMonthYear(UUID creditCardId, int month, int year) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        return springBillingCycleRepository.findByCreditCardIdAndDueDateBetween(creditCardId, start, end)
                .stream()
                .findFirst() // Retorna a primeira fatura que coincidir
                .map(BillingCycleJpaEntity::toDomain)
                .orElse(null);
    }
}