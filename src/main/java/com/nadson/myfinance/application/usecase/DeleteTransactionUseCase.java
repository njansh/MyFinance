package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.DeleteTransactionPort;
import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.domain.enums.TransactionType;
import com.nadson.myfinance.domain.exception.AccountNotFoundException;
import com.nadson.myfinance.domain.exception.TransactionNotFoundException;

import java.util.UUID;

public class DeleteTransactionUseCase implements DeleteTransactionPort {
    private final TransactionRepositoryPort transactionRepository;
    private final AccountRepositoryPort accountRepository;

    public DeleteTransactionUseCase(TransactionRepositoryPort transactionRepository, AccountRepositoryPort accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public void execute(UUID transactionID) {
      Transaction transaction=  transactionRepository.findById(transactionID);
      if(transaction==null){
          throw new TransactionNotFoundException(transactionID);
      }
        Account account = accountRepository.findById(transaction.getAccountId());
      if(account==null){
    throw new AccountNotFoundException(transaction.getAccountId());
      }

        if (transaction.getType() == TransactionType.EXPENSE) {
            account.deposit(transaction.getAmount());
        } else {
            account.withdraw(transaction.getAmount());
        }
        accountRepository.save(account);
        transactionRepository.deleteById(transaction.getTransactionId());

    }
}
