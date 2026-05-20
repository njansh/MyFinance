package com.nadson.myfinance.application.port.in;

import com.nadson.myfinance.domain.records.CreditCardWithBalanceDTO;
import java.util.UUID;

public interface GetCreditCardPort {
    CreditCardWithBalanceDTO execute(UUID userId, UUID id);
}