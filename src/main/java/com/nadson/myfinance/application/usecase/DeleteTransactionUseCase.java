package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.DeleteTransactionPort;
import com.nadson.myfinance.application.port.in.RevertTransactionInGoalPort;
import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
import com.nadson.myfinance.application.port.out.BudgetRepositoryPort;
import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.entity.Budget;
import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.domain.enums.TransactionType;
import com.nadson.myfinance.domain.exception.AccountNotFoundException;
import com.nadson.myfinance.domain.exception.TransactionNotFoundException;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

public class DeleteTransactionUseCase implements DeleteTransactionPort {
    private final TransactionRepositoryPort transactionRepository;
    private final AccountRepositoryPort accountRepository;
    private final BudgetRepositoryPort budgetRepository;
    private final RevertTransactionInGoalPort revertTransactionInGoal; // Injetando o caso de uso

    public DeleteTransactionUseCase(
            TransactionRepositoryPort transactionRepository,
            AccountRepositoryPort accountRepository,
            BudgetRepositoryPort budgetRepository,
            RevertTransactionInGoalPort revertTransactionInGoal) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.budgetRepository = budgetRepository;
        this.revertTransactionInGoal = revertTransactionInGoal;
    }

    @Override
    @Transactional
    public void execute(UUID transactionID) {
        Transaction transaction = transactionRepository.findById(transactionID);
        if (transaction == null) {
            throw new TransactionNotFoundException(transactionID);
        }

        if (transaction.isTransfer()) {
            List<Transaction> transferTransactions = transactionRepository.findAllByTransferID(transaction.getTransferID());
            for (Transaction t : transferTransactions) {
                reverseTransaction(t);
                transactionRepository.deleteById(t.getTransactionId());
            }
        } else {
            reverseTransaction(transaction);
            transactionRepository.deleteById(transactionID);
        }
    }

    private void reverseTransaction(Transaction t) {
        Account acc = accountRepository.findById(t.getAccountId());
        if (acc == null) {
            throw new AccountNotFoundException(t.getAccountId());
        }
        if (t.getType() == TransactionType.EXPENSE) {
            acc.deposit(t.getAmount());
        } else {
            acc.withdraw(t.getAmount());
        }
        accountRepository.save(acc);

        if (t.getType() == TransactionType.EXPENSE && t.getCategoryId() != null) {
            UUID userId = accountRepository.findUserIdByAccountId(t.getAccountId());
            Budget budget = budgetRepository.findByUserIdAndCategoryIdAndMonthAndYear(
                    userId, t.getCategoryId(), t.getDate().getMonthValue(), t.getDate().getYear());
            if (budget != null) {
                budget.removeExpense(t.getAmount());
                budgetRepository.save(budget);
            }
        }

        // Delegação simples e direta:
        revertTransactionInGoal.execute(t);
    }
}