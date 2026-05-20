package com.nadson.myfinance.application.port.in;

import com.nadson.myfinance.domain.records.BillingCycleDetailsDTO;
import java.util.UUID;

public interface GetBillingCycleDetailsPort {
    BillingCycleDetailsDTO execute(UUID userId, UUID creditCardId, UUID billingCycleId);
}