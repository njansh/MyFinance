package com.nadson.myfinance.application.port.in;

import com.nadson.myfinance.domain.entity.CreditCard;
import java.math.BigDecimal;
import java.util.UUID;

public interface CreateCreditCardPort {

    CreditCard execute(UUID userId, String name, BigDecimal creditLimit, int closingDay, int dueDay, UUID accountId);
}