package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.ProcessTransactionInBudgetPort;
import com.nadson.myfinance.application.port.in.UpdateTransactionPort;
import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
import com.nadson.myfinance.application.port.out.BudgetRepositoryPort;
import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
import com.nadson.myfinance.application.usecase.CreateTransactionUseCase.TransactionResult;
import com.nadson.myfinance.domain.entity.Budget;
import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.domain.enums.TransactionType;
import com.nadson.myfinance.domain.exception.TransactionNotFoundException;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class UpdateTransactionUseCase implements UpdateTransactionPort {

    private final TransactionRepositoryPort transactionRepo;
    private final AccountRepositoryPort accountRepo;
    private final BudgetRepositoryPort budgetRepo;
    private final ProcessTransactionInBudgetPort processTransactionInBudget;

    public UpdateTransactionUseCase(TransactionRepositoryPort transactionRepo,
                                    AccountRepositoryPort accountRepo,
                                    BudgetRepositoryPort budgetRepo,
                                    ProcessTransactionInBudgetPort processTransactionInBudget) {
        this.transactionRepo = transactionRepo;
        this.accountRepo = accountRepo;
        this.budgetRepo = budgetRepo;
        this.processTransactionInBudget = processTransactionInBudget;
    }

    @Override
    @Transactional
    public TransactionResult execute(UUID transactionId, String description, BigDecimal amount, LocalDateTime date, TransactionType type, UUID accountId, UUID categoryId) {
        Transaction oldTx = transactionRepo.findById(transactionId);
        if (oldTx == null) throw new TransactionNotFoundException(transactionId);

        if (oldTx.isTransfer()) {
            List<Transaction> transferTransactions = transactionRepo.findAllByTransferID(oldTx.getTransferID());

            for (Transaction tx : transferTransactions) {
                BigDecimal reversal = tx.getType() == TransactionType.EXPENSE ? tx.getAmount() : tx.getAmount().negate();
                accountRepo.updateBalanceAtomic(tx.getAccountId(), reversal);

                tx.updateDetails(description, amount, date, tx.getType(), tx.getAccountId(), categoryId);
                transactionRepo.save(tx);

                BigDecimal adjustment = tx.getType() == TransactionType.EXPENSE ? amount.negate() : amount;
                accountRepo.updateBalanceAtomic(tx.getAccountId(), adjustment);
            }
            return new TransactionResult(oldTx, "Transferência atualizada com sucesso.");
        }

        BigDecimal reversal = oldTx.getType() == TransactionType.EXPENSE ? oldTx.getAmount() : oldTx.getAmount().negate();
        accountRepo.updateBalanceAtomic(oldTx.getAccountId(), reversal);

        BigDecimal adjustment = type == TransactionType.EXPENSE ? amount.negate() : amount;
        accountRepo.updateBalanceAtomic(accountId, adjustment);

        if (oldTx.getType() == TransactionType.EXPENSE && oldTx.getCategoryId() != null) {
            UUID userId = accountRepo.findUserIdByAccountId(oldTx.getAccountId());
            Budget oldBudget = budgetRepo.findByUserIdAndCategoryIdAndMonthAndYear(
                    userId, oldTx.getCategoryId(), oldTx.getDate().getMonthValue(), oldTx.getDate().getYear());
            if (oldBudget != null) {
                oldBudget.removeExpense(oldTx.getAmount());
                budgetRepo.save(oldBudget);
            }
        }

        oldTx.updateDetails(description, amount, date, type, accountId, categoryId);
        Transaction updatedTx = transactionRepo.save(oldTx);

        String alert = null;
        if (updatedTx.getType() == TransactionType.EXPENSE && updatedTx.getCategoryId() != null) {
            alert = processTransactionInBudget.execute(updatedTx);
        }

        return new TransactionResult(updatedTx, alert);
    }
}
