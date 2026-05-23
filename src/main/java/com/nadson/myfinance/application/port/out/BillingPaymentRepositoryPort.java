package com.nadson.myfinance.application.port.out;

import com.nadson.myfinance.domain.entity.BillingPayment;

public interface BillingPaymentRepositoryPort {
    BillingPayment save(BillingPayment payment);
}