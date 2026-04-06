package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.TransferPort;
import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.domain.enums.TransactionType;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

public class TransferUseCase implements TransferPort {
    private final AccountRepositoryPort accountRepositoryPort;
    private final TransactionRepositoryPort transactionRepositoryPort;
    public TransferUseCase(AccountRepositoryPort accountRepositoryPort, TransactionRepositoryPort transactionRepositoryPort) {
        this.accountRepositoryPort = accountRepositoryPort;
        this.transactionRepositoryPort = transactionRepositoryPort;
    }
    @Override
    @Transactional
    public void execute(UUID senderAccountId, UUID receiverAccountId, BigDecimal amount) {
        Account senderAccount=accountRepositoryPort.findById(senderAccountId);
        Account receiverAccount=accountRepositoryPort.findById(receiverAccountId);
        if (senderAccount == null || receiverAccount == null) {
            throw new IllegalArgumentException("Sender or receiver account not found");
    }
        senderAccount.withdraw(amount);
        receiverAccount.deposit(amount);

        Transaction debit = new Transaction(senderAccountId, amount, TransactionType.EXPENSE, "Transfer to " + receiverAccount.getName());
        Transaction credit = new Transaction(receiverAccountId, amount, TransactionType.INCOME, "Transfer from " + senderAccount.getName());

        accountRepositoryPort.save(senderAccount);
        accountRepositoryPort.save(receiverAccount);

        transactionRepositoryPort.save(debit);
        transactionRepositoryPort.save(credit);
    }
}
