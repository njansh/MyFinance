package com.nadson.myfinance.infrastructure.adapter.persistence.entity;

import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.enums.AccountType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "accounts")
public class AccountJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountType type;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal balance;

    @Version
    private Long version;

    public AccountJpaEntity() {
    }

    public AccountJpaEntity(UUID id, UUID userId, AccountType type, String name, BigDecimal balance) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.name = name;
        this.balance = balance;
    }

    public static AccountJpaEntity fromDomain(Account account) {
        return new AccountJpaEntity(
            account.getAccountId(),
            account.getUserId(),
            account.getType(),
            account.getName(),
            account.getBalance()
        );
    }

    public Account toDomain() {
        return new Account(
            this.id,
            this.userId,
            this.type,
            this.name,
            this.balance
        );
    }
    
    
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
