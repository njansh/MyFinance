package com.nadson.myfinance.application.port.in;

import com.nadson.myfinance.domain.entity.CreditCard;

import java.math.BigDecimal;
import java.util.UUID;

public interface CreateCreditCardPort {

    CreditCard execute(String name, BigDecimal bigDecimal, int i, int i1, UUID uuid);
}
