package com.nadson.myfinance.infrastructure.adapter.persistence.repository;

import com.nadson.myfinance.domain.enums.TransactionStatus;
import com.nadson.myfinance.domain.enums.TransactionType;
import com.nadson.myfinance.infrastructure.adapter.persistence.entity.TransactionJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringTransactionRepository extends JpaRepository<TransactionJpaEntity, UUID> {

    Page<TransactionJpaEntity> findByAccountId(UUID accountId, Pageable pageable);

    Page<TransactionJpaEntity> findByAccountIdAndDateBetween(UUID accountId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    List<TransactionJpaEntity> findAllByAccountId(UUID accountId);

    List<TransactionJpaEntity> findAllByAccountIdAndDateBetween(UUID accountId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT COUNT(t) FROM TransactionJpaEntity t WHERE " +
            "t.accountId = :accountId AND " +
            "t.date = :date AND " +
            "t.amount = :amount AND " +
            "t.description = :description AND " +
            "t.accountBalanceAfter = :balanceAfter")
    long countWithAllFilters(
            @Param("accountId") java.util.UUID accountId,
            @Param("date") java.time.LocalDateTime date,
            @Param("amount") java.math.BigDecimal amount,
            @Param("description") String description,
            @Param("balanceAfter") java.math.BigDecimal balanceAfter
    );

    @Query("SELECT COUNT(t) > 0 FROM TransactionJpaEntity t WHERE " +
            "t.accountId = :accountId AND " +
            "t.date = :date AND " +
            "t.amount = :amount")
    boolean existsTransferCounterpart(
            @Param("accountId") UUID accountId,
            @Param("date") LocalDateTime date,
            @Param("amount") BigDecimal amount
    );

    @Query("SELECT t FROM TransactionJpaEntity t WHERE " +
            "t.accountId = :accountId AND " +
            "t.date = :date AND " +
            "t.amount = :amount")
    List<TransactionJpaEntity> findPossibleDuplicates(
            @Param("accountId") UUID accountId,
            @Param("date") LocalDateTime date,
            @Param("amount") BigDecimal amount
    );

    @Query("SELECT COALESCE(c.name, 'Sem Categoria'), SUM(t.amount) " +
            "FROM TransactionJpaEntity t LEFT JOIN CategoryJpaEntity c ON t.categoryId = c.id " +
            "WHERE t.accountId = :accountId AND t.type = :type AND t.status = 'COMPLETED' " +
            "AND t.isTransfer = false " +
            "GROUP BY c.name")
    List<Object> sumAmountByCategoryAndType(@Param("accountId") UUID accountId, @Param("type") TransactionType type);

    @Query("SELECT t FROM TransactionJpaEntity t WHERE " +
            "t.accountId = :accountId AND " +
            "t.date = :date AND " +
            "t.amount = :amount AND " +
            "t.type = :type AND " +
            "t.accountBalanceAfter IS NULL")
    List<TransactionJpaEntity> findUnmatchedTransactions(
            @Param("accountId") java.util.UUID accountId,
            @Param("date") java.time.LocalDateTime date,
            @Param("amount") java.math.BigDecimal amount,
            @Param("type") com.nadson.myfinance.domain.enums.TransactionType type
    );

    @Query("SELECT COALESCE(c.name, 'Sem Categoria'), SUM(t.amount) " +
            "FROM TransactionJpaEntity t LEFT JOIN CategoryJpaEntity c ON t.categoryId = c.id " +
            "WHERE t.accountId = :accountId AND t.type = :type AND t.date BETWEEN :startDate AND :endDate " +
            "AND t.status = 'COMPLETED' " +
            "AND t.isTransfer = false " +
            "GROUP BY c.name")
    List<Object> sumAmountByCategoryAndTypeAndDateBetween(@Param("accountId") UUID accountId, @Param("type") TransactionType type, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    List<TransactionJpaEntity> findByTransferID(UUID transferID);

    Page<TransactionJpaEntity> findByAccountIdAndDescriptionContainingIgnoreCase(UUID accountId, String description, Pageable pageable);

    Page<TransactionJpaEntity> findByAccountIdAndDateBetweenAndDescriptionContainingIgnoreCase(UUID accountId, LocalDateTime startDate, LocalDateTime endDate, String description, Pageable pageable);

    @Query("SELECT SUM(t.amount) FROM TransactionJpaEntity t " +
            "WHERE t.accountId IN :accountIds " +
            "AND t.date BETWEEN :start AND :end " +
            "AND t.type = :type AND t.isTransfer = false AND t.status = 'COMPLETED'")
    BigDecimal sumTransactionsByAccountsAndPeriod(
            @Param("accountIds") List<UUID> accountIds,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("type") TransactionType type);

    @Query("SELECT SUM(t.amount) FROM TransactionJpaEntity t " +
            "WHERE t.accountId IN :investmentAccountIds " +
            "AND t.date BETWEEN :start AND :end " +
            "AND t.isTransfer = true AND t.type = 'INCOME'")
    BigDecimal sumSavingsByAccountsAndPeriod(
            @Param("investmentAccountIds") List<UUID> investmentAccountIds,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT SUM(t.amount) FROM TransactionJpaEntity t WHERE t.accountId IN :accountIds AND t.date < :date AND t.type = :type AND t.status = 'COMPLETED'")
    BigDecimal sumTransactionsBeforeDate(
            @Param("accountIds") List<UUID> accountIds,
            @Param("date") LocalDateTime date,
            @Param("type") TransactionType type
    );

    @Query("SELECT t FROM TransactionJpaEntity t JOIN AccountJpaEntity a ON t.accountId = a.id " +
            "WHERE a.userId = :userId AND t.status = 'PENDING' " +
            "AND t.date >= :startDate AND t.date <= :endDate")
    List<TransactionJpaEntity> findAllPendingByUserIdAndMonth(
            @Param("userId") UUID userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Modifying
    @Query("DELETE FROM TransactionJpaEntity t WHERE t.templateId = :templateId AND t.status = :status")
    void deleteByTemplateIdAndStatus(@Param("templateId") UUID templateId, @Param("status") TransactionStatus status);

    @Query("SELECT t FROM TransactionJpaEntity t " +
            "JOIN AccountJpaEntity a ON t.accountId = a.id " +
            "WHERE a.userId = :userId " +
            "AND t.categoryId = :categoryId " +
            "AND EXTRACT(MONTH FROM t.date) = :month " +
            "AND EXTRACT(YEAR FROM t.date) = :year")
    List<TransactionJpaEntity> findByUserIdAndCategoryIdAndMonthAndYear(
            @Param("userId") UUID userId,
            @Param("categoryId") UUID categoryId,
            @Param("month") int month,
            @Param("year") int year
    );
    @Modifying
    @Query("DELETE FROM TransactionJpaEntity t WHERE t.accountId = :accountId")
    void deleteAllByAccountId(@Param("accountId") UUID accountId);
    @Modifying
    @Query("DELETE FROM TransactionJpaEntity t WHERE t.transferID IN " +
            "(SELECT t2.transferID FROM TransactionJpaEntity t2 WHERE t2.accountId = :accountId AND t2.transferID IS NOT NULL)")
    void deleteTransferCounterpartsByAccountId(@Param("accountId") UUID accountId);
}