package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.UpdateTransactionPort;
import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
import com.nadson.myfinance.application.port.out.CategoryRepositoryPort;
import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.domain.enums.TransactionType;
import com.nadson.myfinance.domain.exception.TransactionNotFoundException;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class UpdateTransactionUseCase implements UpdateTransactionPort {

    private final TransactionRepositoryPort transactionRepo;
    private final CategoryRepositoryPort categoryRepo;
    private final AccountRepositoryPort accountRepo;

    public UpdateTransactionUseCase(TransactionRepositoryPort transactionRepo,
                                    CategoryRepositoryPort categoryRepo,
                                    AccountRepositoryPort accountRepo) {
        this.transactionRepo = transactionRepo;
        this.categoryRepo = categoryRepo;
        this.accountRepo = accountRepo;
    }

    @Override
    @Transactional
    public void execute(UUID transactionId, String description, BigDecimal amount, LocalDateTime date, TransactionType type, UUID accountId, UUID categoryId) {
        Transaction oldTx = transactionRepo.findById(transactionId);
        if (oldTx == null) {
            throw new TransactionNotFoundException(transactionId);
        }

        BigDecimal reversal = oldTx.getType() == TransactionType.EXPENSE? oldTx.getAmount() : oldTx.getAmount().negate();
        accountRepo.updateBalanceAtomic(oldTx.getAccountId(), reversal);

        BigDecimal adjustment = type == TransactionType.EXPENSE? amount.negate() : amount;
        accountRepo.updateBalanceAtomic(accountId, adjustment);

        oldTx.updateDetails(description, amount, date, type, accountId, categoryId);
        transactionRepo.save(oldTx);
    }
}