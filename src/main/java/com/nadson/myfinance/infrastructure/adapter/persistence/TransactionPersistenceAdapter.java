package com.nadson.myfinance.infrastructure.adapter.persistence;

import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.infrastructure.adapter.persistence.entity.TransactionJpaEntity;
import com.nadson.myfinance.infrastructure.adapter.persistence.repository.SpringTransactionRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TransactionPersistenceAdapter implements TransactionRepositoryPort {
    private final SpringTransactionRepository springTransactionRepository;
    
    public TransactionPersistenceAdapter(SpringTransactionRepository springTransactionRepository) {
        this.springTransactionRepository = springTransactionRepository;
}


    @Override
    public Transaction save(Transaction transaction) {
        TransactionJpaEntity entity = new TransactionJpaEntity(transaction);
        TransactionJpaEntity savedEntity = springTransactionRepository.save(entity);
        return savedEntity.toDomain();
    }

    @Override
    public Transaction findById(UUID transactionId) {
        return springTransactionRepository.findById(transactionId)
                .map(TransactionJpaEntity::toDomain)
                .orElse(null);
    }

    @Override
    public List<Transaction> findByAccountId(UUID accountId) {
        return springTransactionRepository.findByAccountId(accountId)
                .stream()
                .map(TransactionJpaEntity::toDomain)
                .collect(Collectors.toList());
    }
}

