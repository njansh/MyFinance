package com.nadson.myfinance.application.port.in;

import java.math.BigDecimal;
import java.util.UUID;

public interface BillingProcessPaymentPort {
    void BillingProcessPayment(UUID userId, UUID cardId, UUID cycleId, UUID accountId, BigDecimal amountToPay);
}