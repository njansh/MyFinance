package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.ProcessTransactionInBudgetPort;
import com.nadson.myfinance.application.port.in.ProcessTransactionInGoalPort;
import com.nadson.myfinance.application.port.in.RevertTransactionInGoalPort;
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
    private final RevertTransactionInGoalPort revertTransactionInGoal;
    private final ProcessTransactionInGoalPort processTransactionInGoal;

    public UpdateTransactionUseCase(TransactionRepositoryPort transactionRepo,
                                    AccountRepositoryPort accountRepo,
                                    BudgetRepositoryPort budgetRepo,
                                    ProcessTransactionInBudgetPort processTransactionInBudget,
                                    RevertTransactionInGoalPort revertTransactionInGoal,
                                    ProcessTransactionInGoalPort processTransactionInGoal) {
        this.transactionRepo = transactionRepo;
        this.accountRepo = accountRepo;
        this.budgetRepo = budgetRepo;
        this.processTransactionInBudget = processTransactionInBudget;
        this.revertTransactionInGoal = revertTransactionInGoal;
        this.processTransactionInGoal = processTransactionInGoal;
    }

    @Override
    @Transactional
    public TransactionResult execute(UUID transactionId, String description, BigDecimal amount, LocalDateTime date, TransactionType type, UUID accountId, UUID categoryId) {
        Transaction oldTx = transactionRepo.findById(transactionId);
        if (oldTx == null) throw new TransactionNotFoundException(transactionId);

        if (oldTx.isTransfer()) {
            List<Transaction> transferTransactions = transactionRepo.findAllByTransferID(oldTx.getTransferID());

            for (Transaction tx : transferTransactions) {
                revertAccountBalance(tx);
                revertTransactionInGoal.execute(tx);

                tx.updateDetails(description, amount, date, tx.getType(), tx.getAccountId(), categoryId);
                transactionRepo.save(tx);

                applyAccountBalance(tx.getAccountId(), tx.getAmount(), tx.getType());
                processTransactionInGoal.execute(tx);
            }
            return new TransactionResult(oldTx, "Transfer updated successfully.");
        }

        revertAccountBalance(oldTx);
        revertTransactionInGoal.execute(oldTx);

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

        applyAccountBalance(updatedTx.getAccountId(), updatedTx.getAmount(), updatedTx.getType());

        String alert = null;
        if (updatedTx.getType() == TransactionType.EXPENSE && updatedTx.getCategoryId() != null) {
            alert = processTransactionInBudget.execute(updatedTx);
        }

        processTransactionInGoal.execute(updatedTx);

        return new TransactionResult(updatedTx, alert);
    }

    private void revertAccountBalance(Transaction tx) {
        BigDecimal reversal = tx.getType() == TransactionType.EXPENSE ? tx.getAmount() : tx.getAmount().negate();
        accountRepo.updateBalanceAtomic(tx.getAccountId(), reversal);
    }

    private void applyAccountBalance(UUID accId, BigDecimal amt, TransactionType tType) {
        BigDecimal adjustment = tType == TransactionType.EXPENSE ? amt.negate() : amt;
        accountRepo.updateBalanceAtomic(accId, adjustment);
    }
}
