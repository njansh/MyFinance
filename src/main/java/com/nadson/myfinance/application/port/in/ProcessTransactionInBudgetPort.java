package com.nadson.myfinance.application.port.in;

import com.nadson.myfinance.domain.entity.Transaction;

public interface ProcessTransactionInBudgetPort {
    void execute(Transaction transaction);
}