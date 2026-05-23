package com.nadson.myfinance.application.port.out;

import com.nadson.myfinance.domain.entity.BillingPayment;

import java.util.UUID;

public interface BillingPaymentRepositoryPort {
    BillingPayment save(BillingPayment payment);
    void deleteAllByUserId(UUID userId);

    void deleteAllByAccountId(UUID accId);
}