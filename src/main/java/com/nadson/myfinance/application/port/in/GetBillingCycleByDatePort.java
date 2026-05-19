package com.nadson.myfinance.application.port.in;

import com.nadson.myfinance.domain.records.BillingCycleDetailsDTO;
import java.util.UUID;

public interface GetBillingCycleByDatePort {
    BillingCycleDetailsDTO execute(UUID userId, UUID creditCardId, int month, int year);
}