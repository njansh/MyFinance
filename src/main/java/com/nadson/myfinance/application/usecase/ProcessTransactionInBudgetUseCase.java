package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.ProcessTransactionInBudgetPort;
import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
import com.nadson.myfinance.application.port.out.BudgetRepositoryPort;
import com.nadson.myfinance.domain.entity.Budget;
import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.domain.enums.TransactionType;
import java.time.LocalDateTime;
import java.util.UUID;

public class ProcessTransactionInBudgetUseCase implements ProcessTransactionInBudgetPort {
    private final BudgetRepositoryPort budgetRepository;
    private final AccountRepositoryPort accountRepository;

    public ProcessTransactionInBudgetUseCase(BudgetRepositoryPort budgetRepository, AccountRepositoryPort accountRepository) {
        this.budgetRepository = budgetRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public String execute(Transaction transaction) {
        if (transaction.getType() != TransactionType.EXPENSE) return null;

        UUID userId = accountRepository.findUserIdByAccountId(transaction.getAccountId());
        LocalDateTime date = transaction.getDate();
        Budget budget = budgetRepository.findByUserIdAndCategoryIdAndMonthAndYear(
                userId, transaction.getCategoryId(), date.getMonthValue(), date.getYear());

        if (budget != null) {
            budget.addExpense(transaction.getAmount());
            budgetRepository.save(budget);

            if (budget.shouldAlertOneHundredPercent()) return "Budget limit exceeded!";
            if (budget.shouldAlertEightyPercent()) return "You have reached 80% of your budget!";
        }
        return null;    
    }
}