package com.nadson.myfinance.infrastructure.adapter.persistence;

import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.infrastructure.adapter.persistence.entity.TransactionJpaEntity;
import com.nadson.myfinance.infrastructure.adapter.persistence.repository.SpringTransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
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
    public Page<Transaction> findByAccountId(UUID accountId, Pageable pageable) {
        return repository.findByAccountId(accountId, pageable)
                .map(TransactionJpaEntity::toDomain); // O Page já tem o map!
    }

    @Override
    public Page<Transaction> findByAccountIdAndDateBetween(UUID accountId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return repository.findByAccountIdAndDateBetween(accountId, startDate, endDate, pageable)
                .map(TransactionJpaEntity::toDomain);
    }
    @Override
    public List<Transaction> findAllByAccountId(UUID accountId) {
        return repository.findAllByAccountId(accountId)
                .stream()
                .map(TransactionJpaEntity::toDomain)
                .toList();
    }

    @Override
    public List<Transaction> findAllByAccountIdAndDateBetween(UUID accountId, LocalDateTime startDate, LocalDateTime endDate) {
        return repository.findAllByAccountIdAndDateBetween(accountId, startDate, endDate)
                .stream()
                .map(TransactionJpaEntity::toDomain)
                .toList();
    }
    @Override
public List<Transaction> findAllByTransferID(UUID transferID) {
    return repository.findByTransferID(transferID)
            .stream()
            .map(TransactionJpaEntity::toDomain)
            .toList();
}

    @Override
    public void deleteById(UUID transactionId) {
        repository.deleteById(transactionId);

    }
}
