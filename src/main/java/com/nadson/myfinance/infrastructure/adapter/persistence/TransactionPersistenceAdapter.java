package com.nadson.myfinance.infrastructure.adapter.persistence;

import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.infrastructure.adapter.persistence.entity.TransactionJpaEntity;
import com.nadson.myfinance.infrastructure.adapter.persistence.repository.SpringTransactionRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
@Component
public class TransactionPersistenceAdapter implements TransactionRepositoryPort {
    private final SpringTransactionRepository repository;

    public TransactionPersistenceAdapter(SpringTransactionRepository repository) {
        this.repository = repository;
    }


    @Override
    public Transaction save(Transaction transaction) {
        TransactionJpaEntity entity = new TransactionJpaEntity(transaction);
        return repository.save(entity).toDomain();
    }

    @Override
    public Transaction findById(UUID transactionId) {
        return repository.findById(transactionId)
                .map(TransactionJpaEntity::toDomain)
                .orElse(null);
    }

    @Override
    public List<Transaction> findByAccountId(UUID accountId) {
        return repository.findByAccountId(accountId).stream().map(TransactionJpaEntity::toDomain).toList();
    }
}
