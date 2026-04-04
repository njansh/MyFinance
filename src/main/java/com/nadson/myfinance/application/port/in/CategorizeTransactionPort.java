package com.nadson.myfinance.application.port.in;

import com.nadson.myfinance.domain.entity.Transaction;

import java.util.UUID;

public interface CategorizeTransactionPort {
    Transaction execute(UUID transactionId, UUID categoryId);
}
