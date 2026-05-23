package com.nadson.myfinance.application.port.in;

import java.util.UUID;

public interface DeleteCreditCardPort {
    void execute(UUID creditCardId,UUID userId);

}
