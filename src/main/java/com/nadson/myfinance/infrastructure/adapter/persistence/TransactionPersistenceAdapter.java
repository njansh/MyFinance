package com.nadson.myfinance.infrastructure.adapter.persistence;

import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.infrastructure.adapter.persistence.entity.TransactionJpaEntity;
import com.nadson.myfinance.infrastructure.adapter.persistence.repository.SpringTransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
                .map(TransactionJpaEntity::toDomain);
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
    public long count(UUID accountId, LocalDateTime date, BigDecimal amount, String description, BigDecimal accountBalanceAfter) {
        return repository.countWithAllFilters(
                accountId, date, amount, description, accountBalanceAfter);
    }

    @Override
    public List<Transaction> findPossibleDuplicates(UUID accountId, LocalDateTime date, BigDecimal amount) {
        return repository.findPossibleDuplicates(accountId, date, amount)
                .stream()
                .map(TransactionJpaEntity::toDomain)
                .toList();
    }

    @Override
    public void updateBalance(UUID transactionId, BigDecimal balanceAfter) {
        var entity = repository.findById(transactionId).orElseThrow(() ->
                new RuntimeException("Transação não encontrada para atualização de saldo: " + transactionId));
        entity.setAccountBalanceAfter(balanceAfter);
        repository.save(entity);
    }

    @Override
    public boolean existsTransferCounterpart(UUID accountId, LocalDateTime date, BigDecimal amount) {
        return repository.existsTransferCounterpart(accountId, date, amount);
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
    @Override
    public Page<Transaction> findByAccountIdAndDescription(UUID accountId, String description, Pageable pageable) {
        return repository.findByAccountIdAndDescriptionContainingIgnoreCase(accountId, description, pageable)
                .map(TransactionJpaEntity::toDomain);
    }

    @Override
    public Page<Transaction> findByAccountIdAndDateBetweenAndDescription(UUID accountId, LocalDateTime startDate, LocalDateTime endDate, String description, Pageable pageable) {
        return repository.findByAccountIdAndDateBetweenAndDescriptionContainingIgnoreCase(accountId, startDate, endDate, description, pageable)
                .map(TransactionJpaEntity::toDomain);
    }
    @Override
    public java.util.Map<String, BigDecimal> getSumByCategoryAndType(UUID accountId, com.nadson.myfinance.domain.enums.TransactionType type) {
        List<Object> results = repository.sumAmountByCategoryAndType(accountId, type);
        return mapResults(results);
    }

    @Override
    public java.util.Map<String, BigDecimal> getSumByCategoryAndTypeAndDateBetween(UUID accountId, com.nadson.myfinance.domain.enums.TransactionType type, LocalDateTime startDate, LocalDateTime endDate) {
        List<Object> results = repository.sumAmountByCategoryAndTypeAndDateBetween(accountId, type, startDate, endDate);
        return mapResults(results);
    }
    @Override
    public Transaction findFirstUnmatchedTransaction(UUID accountId, LocalDateTime date, BigDecimal amount) {
        return repository.findUnmatchedTransactions(accountId, date, amount)
                .stream()
                .findFirst()
                .map(TransactionJpaEntity::toDomain)
                .orElse(null);
    }

    private java.util.Map<String, BigDecimal> mapResults(List<Object> results) {
        java.util.Map<String, BigDecimal> map = new java.util.HashMap<>();

        for (Object result : results) {
            Object[] row = (Object[]) result;

            String categoryName = (row[0] != null) ? row[0].toString() : "Sem Categoria";

            BigDecimal sum = (row[1] != null) ? new BigDecimal(row[1].toString()) : BigDecimal.ZERO;

            map.put(categoryName, sum);
        }
        return map;
    }
}