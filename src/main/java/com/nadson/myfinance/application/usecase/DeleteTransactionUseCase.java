package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.DeleteTransactionPort;
import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
import com.nadson.myfinance.domain.entity.Account;
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

    public DeleteTransactionUseCase(TransactionRepositoryPort transactionRepository, AccountRepositoryPort accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional
    public void execute(UUID transactionID) {
      Transaction transaction=  transactionRepository.findById(transactionID);
      if(transaction==null){
          throw new TransactionNotFoundException(transactionID);
      }
      
    if(transaction.isTransfer()) {
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
        acc.deposit(t.getAmount()); // Devolve o gasto
    } else {
        acc.withdraw(t.getAmount()); // Remove o ganho
    }
    accountRepository.save(acc);
}
}
