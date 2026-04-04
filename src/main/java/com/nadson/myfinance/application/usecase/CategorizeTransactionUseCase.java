package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.CategorizeTransactionPort;
import com.nadson.myfinance.application.port.out.CategoryRepositoryPort;
import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
import com.nadson.myfinance.domain.entity.Transaction;

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
            throw new IllegalArgumentException("Transaction not found");
        }
        if (categoryRepositoryPort.findById(categoryId) == null) {
            throw new IllegalArgumentException("Category not found");
        }
        transaction.updateCategory(categoryId);
        return transactionRepositoryPort.save(transaction);
    }
}
