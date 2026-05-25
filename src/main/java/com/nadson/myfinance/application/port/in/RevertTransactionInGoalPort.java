package com.nadson.myfinance.application.port.in;

import com.nadson.myfinance.domain.entity.Transaction;

public interface RevertTransactionInGoalPort {
    void execute(Transaction transaction);
}