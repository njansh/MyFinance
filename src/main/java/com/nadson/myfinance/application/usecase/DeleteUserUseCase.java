package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.DeleteAccountPort;
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
    private final DeleteAccountPort deleteAccountPort;

    public DeleteUserUseCase(UserRepositoryPort userRepo, AccountRepositoryPort accountRepo,
                             CategoryRepositoryPort categoryRepo, BudgetRepositoryPort budgetRepo,
                             GoalRepositoryPort goalRepo, RecurringTemplateRepositoryPort recurringRepo,
                             DeleteAccountPort deleteAccountPort) {
        this.userRepo = userRepo;
        this.accountRepo = accountRepo;
        this.categoryRepo = categoryRepo;
        this.budgetRepo = budgetRepo;
        this.goalRepo = goalRepo;
        this.recurringRepo = recurringRepo;
        this.deleteAccountPort = deleteAccountPort;
    }

    @Override
    @Transactional
    public void execute(UUID userId) {
        List<Account> accounts = accountRepo.findByUserId(userId);
        for (Account acc : accounts) {
            deleteAccountPort.execute(acc.getAccountId(), userId);
        }

        recurringRepo.deleteAllByUserId(userId);
        goalRepo.deleteAllByUserId(userId);
        budgetRepo.deleteAllByUserId(userId);
        categoryRepo.deleteAllByUserId(userId);

        // 3. Por fim, deleta o próprio usuário
        userRepo.deleteById(userId);
    }
}