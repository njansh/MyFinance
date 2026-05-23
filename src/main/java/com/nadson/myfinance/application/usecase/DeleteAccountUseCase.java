package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.DeleteAccountPort;
import com.nadson.myfinance.application.port.out.*;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.domain.exception.BusinessRuleException;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

public class DeleteAccountUseCase implements DeleteAccountPort {
    private final AccountRepositoryPort accountRepo;
    private final TransactionRepositoryPort transactionRepo;
    private final RecurringTemplateRepositoryPort recurringRepo;
    private final CreditCardRepositoryPort creditCardRepo;
    private final DeleteTransactionUseCase deleteTransactionUseCase;

    public DeleteAccountUseCase(AccountRepositoryPort accountRepo,
                                TransactionRepositoryPort transactionRepo,
                                RecurringTemplateRepositoryPort recurringRepo,
                                CreditCardRepositoryPort creditCardRepo, DeleteTransactionUseCase deleteTransactionUseCase) {
        this.accountRepo = accountRepo;
        this.transactionRepo = transactionRepo;
        this.recurringRepo = recurringRepo;
        this.creditCardRepo = creditCardRepo;
        this.deleteTransactionUseCase = deleteTransactionUseCase;
    }

    @Override
    @Transactional
    public void execute(UUID accountId, UUID userId) {
        Account account = accountRepo.findById(accountId);
        if (account == null || !account.getUserId().equals(userId)) {
            throw new BusinessRuleException("Account not found or access denied.");
        }

        List<Transaction> transactions = transactionRepo.findAllByAccountId(accountId);
        for (Transaction t : transactions) {
            deleteTransactionUseCase.execute(t.getTransactionId());
        }
        creditCardRepo.deleteAllByAccountId(accountId);

        recurringRepo.deleteAllByAccountId(accountId);

        accountRepo.deleteById(accountId);
    }
}