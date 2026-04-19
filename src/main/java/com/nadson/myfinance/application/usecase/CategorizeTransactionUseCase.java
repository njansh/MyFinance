package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.CategorizeTransactionPort;
import com.nadson.myfinance.application.port.out.CategoryRepositoryPort;
import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.domain.exception.CategoryNotFoundException;
import com.nadson.myfinance.domain.exception.TransactionNotFoundException;

import java.util.UUID;

public class CategorizeTransactionUseCase implements CategorizeTransactionPort {
    private final TransactionRepositoryPort transactionRepositoryPort;
    private final CategoryRepositoryPort categoryRepositoryPort;

    public CategorizeTransactionUseCase(TransactionRepositoryPort transactionRepositoryPort, CategoryRepositoryPort categoryRepositoryPort) {
        this.transactionRepositoryPort = transactionRepositoryPort;
        this.categoryRepositoryPort = categoryRepositoryPort;
    }


    @Override
    public Transaction execute(UUID transactionId, UUID categoryId) {
        Transaction transaction = transactionRepositoryPort.findById(transactionId);
        if (transaction == null) {
            throw new TransactionNotFoundException(transactionId);
        }
        if (categoryRepositoryPort.findById(categoryId) == null) {
            throw new CategoryNotFoundException(categoryId);
        }
        transaction.updateCategory(categoryId);
        return transactionRepositoryPort.save(transaction);
    }
}
