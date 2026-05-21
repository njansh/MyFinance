package com.nadson.myfinance.application.port.in;

import com.nadson.myfinance.domain.entity.Transaction;

import java.util.List;
import java.util.UUID;

public interface ListPendingRecurringPort {
    List<Transaction> execute(UUID userId, int month, int year);
}
