package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.DeleteUserPort;
import com.nadson.myfinance.application.port.out.*;
import com.nadson.myfinance.domain.entity.Account;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;

public class DeleteUserUseCase implements DeleteUserPort {
    private final UserRepositoryPort userRepo;
    private final AccountRepositoryPort accountRepo;
    private final CategoryRepositoryPort categoryRepo;
    private final BudgetRepositoryPort budgetRepo;
    private final GoalRepositoryPort goalRepo;
    private final RecurringTemplateRepositoryPort recurringRepo;
    private final TransactionRepositoryPort transactionRepo;
    private final BillingCycleRepositoryPort billingCycleRepo;
    private final BillingPaymentRepositoryPort billingPaymentRepo;
    private final CreditCardRepositoryPort creditCardRepo;

    public DeleteUserUseCase(UserRepositoryPort userRepo, AccountRepositoryPort accountRepo,
                             CategoryRepositoryPort categoryRepo, BudgetRepositoryPort budgetRepo,
                             GoalRepositoryPort goalRepo, RecurringTemplateRepositoryPort recurringRepo,
                             TransactionRepositoryPort transactionRepo, BillingCycleRepositoryPort billingCycleRepo, BillingPaymentRepositoryPort billingPaymentRepo, CreditCardRepositoryPort creditCardRepo) {
        this.userRepo = userRepo;
        this.accountRepo = accountRepo;
        this.categoryRepo = categoryRepo;
        this.budgetRepo = budgetRepo;
        this.goalRepo = goalRepo;
        this.recurringRepo = recurringRepo;
        this.transactionRepo = transactionRepo;
        this.billingCycleRepo = billingCycleRepo;
        this.billingPaymentRepo = billingPaymentRepo;
        this.creditCardRepo = creditCardRepo;
    }
    @Override
    @Transactional
    public void execute(UUID userId) {
        billingPaymentRepo.deleteAllByUserId(userId);
        billingCycleRepo.deleteAllByUserId(userId);

        List<Account> accounts = accountRepo.findByUserId(userId);
        for (Account acc : accounts) {
            UUID accId = acc.getAccountId();

            transactionRepo.deleteAllByAccountId(accId);
            recurringRepo.deleteAllByAccountId(accId);
            creditCardRepo.deleteAllByAccountId(accId);

            accountRepo.deleteById(accId);
        }


        recurringRepo.deleteAllByUserId(userId);
        goalRepo.deleteAllByUserId(userId);
        budgetRepo.deleteAllByUserId(userId);
        categoryRepo.deleteAllByUserId(userId);

        userRepo.deleteById(userId);
    }
}