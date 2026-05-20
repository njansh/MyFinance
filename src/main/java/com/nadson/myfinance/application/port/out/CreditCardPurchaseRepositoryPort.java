package com.nadson.myfinance.application.port.out;

import com.nadson.myfinance.domain.entity.CreditCardPurchase;

import java.util.List;
import java.util.UUID;

public interface CreditCardPurchaseRepositoryPort {
    CreditCardPurchase save(CreditCardPurchase purchase);
    CreditCardPurchase findById(UUID id);
    List<CreditCardPurchase> findByCreditCardId(UUID creditCardId);
}