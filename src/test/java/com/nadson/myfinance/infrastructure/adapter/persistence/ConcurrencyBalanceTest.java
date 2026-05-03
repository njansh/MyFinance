package com.nadson.myfinance.infrastructure.adapter.persistence;

import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.enums.AccountType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ConcurrencyBalanceTest {

    @Autowired private AccountRepositoryPort accountRepo;

    @Test
    void shouldMaintainBalanceIntegrityUnderConcurrentUpdates() throws InterruptedException {
        // Arrange: Conta com saldo 1000
        UUID userId = UUID.randomUUID();
        Account account = accountRepo.save(new Account(userId, "Conta Stress", AccountType.CHECKING));
        accountRepo.updateBalanceAtomic(account.getAccountId(), new BigDecimal("1000.00"));

        int threads = 10;
        BigDecimal amountPerThread = new BigDecimal("10.00");
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);

        // Act: 10 threads retirando 10 reais cada uma ao mesmo tempo
        for (int i = 0; i < threads; i++) {
            executor.execute(() -> {
                try {
                    accountRepo.updateBalanceAtomic(account.getAccountId(), amountPerThread.negate());
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        // Assert: O saldo final DEVE ser 900.00 (1000 - 10*10)
        Account finalAccount = accountRepo.findById(account.getAccountId());
        assertEquals(0, new BigDecimal("900.00").compareTo(finalAccount.getBalance()),
                "O saldo foi corrompido por causa de concorrência!");
    }
}