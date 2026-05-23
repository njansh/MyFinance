package com.nadson.myfinance.infrastructure.adapter.persistence.repository;

import com.nadson.myfinance.infrastructure.adapter.persistence.entity.CreditCardJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpringCreditCardRepository extends JpaRepository<CreditCardJpaEntity, UUID> {
    @Modifying
    @Query("DELETE FROM CreditCardJpaEntity c WHERE c.accountId = :accountId")
    void deleteAllByAccountId(@Param("accountId") UUID accountId);

    List<CreditCardJpaEntity> findByUserId(UUID userId);
}
