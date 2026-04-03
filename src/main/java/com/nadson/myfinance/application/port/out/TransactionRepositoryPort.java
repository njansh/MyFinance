package com.nadson.myfinance.application.port.out;

import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.entity.Transaction;

public interface TransactionRepositoryPort {
    Transaction save(Transaction transaction);
}


