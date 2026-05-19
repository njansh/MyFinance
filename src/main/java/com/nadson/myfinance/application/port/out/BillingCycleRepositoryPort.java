package com.nadson.myfinance.application.port.out;

import com.nadson.myfinance.domain.entity.BillingCycle;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface BillingCycleRepositoryPort {
    BillingCycle save(BillingCycle billingCycle);
    BillingCycle findOpenCycleByCardId(UUID creditCardId, LocalDate installmentDate);
    List<BillingCycle> findUnpaidCyclesByCardId(UUID creditCardId);
    BillingCycle findById(UUID billingCycleId);

}
