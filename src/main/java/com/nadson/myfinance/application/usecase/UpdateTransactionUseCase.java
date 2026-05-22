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
    public TransactionResult execute(UUID transactionId, String description, BigDecimal amount,
                                     LocalDateTime date, TransactionType type, UUID accountId, UUID categoryId) {

        Transaction oldTx = transactionRepo.findById(transactionId);
        if (oldTx == null) {
            throw new TransactionNotFoundException(transactionId);
        }

        // 1. Reverte impacto na conta antiga
        BigDecimal reversal = oldTx.getType() == TransactionType.EXPENSE ? oldTx.getAmount() : oldTx.getAmount().negate();
        accountRepo.updateBalanceAtomic(oldTx.getAccountId(), reversal);

        // 2. Aplica impacto na conta nova (ou na mesma com novo valor)
        BigDecimal adjustment = type == TransactionType.EXPENSE ? amount.negate() : amount;
        accountRepo.updateBalanceAtomic(accountId, adjustment);

        // 3. Reverte impacto no orçamento antigo com segurança
        if (oldTx.getType() == TransactionType.EXPENSE && oldTx.getCategoryId() != null) {
            UUID userId = accountRepo.findUserIdByAccountId(oldTx.getAccountId());
            Budget oldBudget = budgetRepo.findByUserIdAndCategoryIdAndMonthAndYear(
                    userId, oldTx.getCategoryId(), oldTx.getDate().getMonthValue(), oldTx.getDate().getYear());
            if (oldBudget != null) {
                oldBudget.removeExpense(oldTx.getAmount());
                budgetRepo.save(oldBudget);
            }
        }

        // 4. Atualiza e salva a transação
        oldTx.updateDetails(description, amount, date, type, accountId, categoryId);
        Transaction updatedTx = transactionRepo.save(oldTx);

        // 5. Aplica impacto no novo orçamento e pega o alerta
        String alert = null;
        if (updatedTx.getType() == TransactionType.EXPENSE && updatedTx.getCategoryId() != null) {
            alert = processTransactionInBudget.execute(updatedTx);
        }

        return new TransactionResult(updatedTx, alert);
    }
}