package com.nadson.myfinance.application.port.in;

import java.util.UUID;

public interface DeleteTransactionPort {
    void execute(UUID transactionID);
}
