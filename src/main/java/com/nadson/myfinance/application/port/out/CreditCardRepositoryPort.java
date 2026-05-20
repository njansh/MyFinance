package com.nadson.myfinance.application.port.out;

import com.nadson.myfinance.domain.entity.CreditCard;
import java.util.List;
import java.util.UUID;

public interface CreditCardRepositoryPort {
    CreditCard save(CreditCard creditCard);
    CreditCard findById(UUID creditCardId);
    List<CreditCard> findByUserId(UUID userId);

    void deleteAllByAccountId(UUID accountId);
}