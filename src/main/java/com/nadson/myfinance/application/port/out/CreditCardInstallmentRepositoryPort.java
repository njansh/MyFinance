package com.nadson.myfinance.application.port.out;

import com.nadson.myfinance.domain.entity.CreditCardInstallment;

import java.util.List;
import java.util.UUID;

public interface CreditCardInstallmentRepositoryPort {
    CreditCardInstallment save(CreditCardInstallment installment);
    List<CreditCardInstallment> findByBillingCycleId(UUID billingCycleId);

    List<CreditCardInstallment> findByPurchaseId(UUID purchaseId);
}