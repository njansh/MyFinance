package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.GetTransactionPort;
import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
import com.nadson.myfinance.domain.entity.Transaction;

import java.util.UUID;

public class GetTransactionUsecase implements GetTransactionPort
{
    private final TransactionRepositoryPort transactionRepositoryPort;

    public GetTransactionUsecase(TransactionRepositoryPort transactionRepositoryPort) {
        this.transactionRepositoryPort = transactionRepositoryPort;
    }

    @Override
    public Transaction execute(UUID transactionId) {
        Transaction transaction = transactionRepositoryPort.findById(transactionId);
        if (transaction == null) {
            throw new RuntimeException("Transaction with ID " + transactionId + " not found");        }
        return transaction;
    }
}
