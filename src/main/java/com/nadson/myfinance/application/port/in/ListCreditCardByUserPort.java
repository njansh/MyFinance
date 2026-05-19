package com.nadson.myfinance.application.port.in;


import com.nadson.myfinance.domain.records.CreditCardWithBalanceDTO;

import java.util.List;
import java.util.UUID;

public interface ListCreditCardByUserPort {
    List<CreditCardWithBalanceDTO> execute(UUID userId);
}