package com.nadson.myfinance.infrastructure.adapter.persistence.repository;

import com.nadson.myfinance.infrastructure.adapter.persistence.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface SpringUserRepository extends JpaRepository<UserJpaEntity, UUID>
{
    @Modifying
    @Query("DELETE FROM UserJpaEntity u WHERE u.id = :userId")
    void deleteById(@Param("userId") UUID userId);

}
