package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.DeleteAccountPort;
import com.nadson.myfinance.application.port.out.*;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.exception.BusinessRuleException;
import jakarta.transaction.Transactional;
import java.util.UUID;

public class DeleteAccountUseCase implements DeleteAccountPort {
    private final AccountRepositoryPort accountRepo;
    private final TransactionRepositoryPort transactionRepo;
    private final RecurringTemplateRepositoryPort recurringRepo;
    private final CreditCardRepositoryPort creditCardRepo;

    public DeleteAccountUseCase(AccountRepositoryPort accountRepo, TransactionRepositoryPort transactionRepo,
                                RecurringTemplateRepositoryPort recurringRepo, CreditCardRepositoryPort creditCardRepo) {
        this.accountRepo = accountRepo;
        this.transactionRepo = transactionRepo;
        this.recurringRepo = recurringRepo;
        this.creditCardRepo = creditCardRepo;
    }

    @Override
    @Transactional
    public void execute(UUID accountId, UUID userId) {
        Account account = accountRepo.findById(accountId);
        if (account == null ||!account.getUserId().equals(userId)) {
            throw new BusinessRuleException("Conta não encontrada ou acesso negado.");
        }

        // A ordem de exclusão é importante por causa das chaves estrangeiras:
        creditCardRepo.deleteAllByAccountId(accountId); // O Adapter deve apagar os BillingCycles antes dos CreditCards
        transactionRepo.deleteAllByAccountId(accountId);
        recurringRepo.deleteAllByAccountId(accountId);
        accountRepo.deleteById(accountId);
    }
}