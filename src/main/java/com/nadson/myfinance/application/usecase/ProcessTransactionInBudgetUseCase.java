package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.ProcessTransactionInBudgetPort;
import com.nadson.myfinance.application.port.out.BudgetRepositoryPort;
import com.nadson.myfinance.application.port.out.AccountRepositoryPort; // Nova porta necessária
import com.nadson.myfinance.domain.entity.Budget;
import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.domain.enums.TransactionType;
import com.nadson.myfinance.domain.exception.AlertType;
import com.nadson.myfinance.domain.exception.BudgetAlertException;

import java.time.LocalDateTime;
import java.util.UUID;

public class ProcessTransactionInBudgetUseCase implements ProcessTransactionInBudgetPort {

    private final BudgetRepositoryPort budgetRepository;
    private final AccountRepositoryPort accountRepository; //

    public ProcessTransactionInBudgetUseCase(BudgetRepositoryPort budgetRepository, AccountRepositoryPort accountRepository) {
        this.budgetRepository = budgetRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public void execute(Transaction transaction) {
        if (transaction.getType() != TransactionType.EXPENSE) {
            return;
        }

        UUID userId = accountRepository.findUserIdByAccountId(transaction.getAccountId());

        LocalDateTime date = transaction.getDate();

        // 2. Agora temos o userId para buscar o orçamento corretamente
        Budget budget = budgetRepository.findByUserIdAndCategoryIdAndMonthAndYear(
                userId,
                transaction.getCategoryId(),
                date.getMonthValue(),
                date.getYear()
        );

        if (budget != null) {
            budget.addExpense(transaction.getAmount());

            if (budget.shouldAlertOneHundredPercent()) {
                throw new BudgetAlertException("Budget limit exceeded!", AlertType.ONE_HUNDRED_PERCENT);
            } else if (budget.shouldAlertEightyPercent()) {
                throw new BudgetAlertException("You have reached 80% of your budget!", AlertType.EIGHTY_PERCENT);
            }

            budgetRepository.save(budget);
        }
    }
}