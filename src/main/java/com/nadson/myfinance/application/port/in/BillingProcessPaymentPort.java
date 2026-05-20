package com.nadson.myfinance.application.port.in;

import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

public interface BillingProcessPaymentPort {

    @Transactional
    void billingProcessPayment(UUID cycleId, UUID accountId, BigDecimal amountToPay);
}
