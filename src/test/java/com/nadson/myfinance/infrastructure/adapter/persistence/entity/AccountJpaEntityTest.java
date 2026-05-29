package com.nadson.myfinance.infrastructure.adapter.persistence.entity;

import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.enums.AccountType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AccountJpaEntityTest {

    @Test
    @DisplayName("Deve testar construtor vazio, todos os getters e setters")
    void shouldTestEmptyConstructorAndGettersSetters() {
        // Testa o construtor vazio (usado pelo Hibernate)
        AccountJpaEntity entity = new AccountJpaEntity();

        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        AccountType type = AccountType.CHECKING;
        String name = "Conta Principal";
        BigDecimal balance = new BigDecimal("1500.00");
        Long version = 1L;

        // Testa todos os setters
        entity.setId(id);
        entity.setUserId(userId);
        entity.setType(type);
        entity.setName(name);
        entity.setBalance(balance);
        entity.setVersion(version);

        // Testa todos os getters
        assertEquals(id, entity.getId());
        assertEquals(userId, entity.getUserId());
        assertEquals(type, entity.getType());
        assertEquals(name, entity.getName());
        assertEquals(balance, entity.getBalance());
        assertEquals(version, entity.getVersion());
    }

    @Test
    @DisplayName("Deve converter corretamente de Domain para Entity e vice-versa")
    void shouldConvertFromAndToDomain() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        // 1. Instancia um domínio válido
        Account account = new Account(id, userId, AccountType.SAVINGS, "Reserva de Emergência", BigDecimal.TEN);

        // 2. Converte para Entity (Testa o fromDomain e o construtor com argumentos)
        AccountJpaEntity entity = AccountJpaEntity.fromDomain(account);

        assertEquals(id, entity.getId());
        assertEquals(userId, entity.getUserId());
        assertEquals(AccountType.SAVINGS, entity.getType());
        assertEquals("Reserva de Emergência", entity.getName());
        assertEquals(BigDecimal.TEN, entity.getBalance());
        assertNull(entity.getVersion()); // Versão inicialmente é nula antes de salvar no banco

        // 3. Converte de volta para Domain (Testa o toDomain)
        Account convertedAccount = entity.toDomain();

        assertEquals(id, convertedAccount.getAccountId());
        assertEquals(userId, convertedAccount.getUserId());
        assertEquals(AccountType.SAVINGS, convertedAccount.getType());
        assertEquals("Reserva de Emergência", convertedAccount.getName());
        assertEquals(BigDecimal.TEN, convertedAccount.getBalance());
    }
}