package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.TransferPort;
import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.domain.enums.TransactionType;
import com.nadson.myfinance.domain.exception.AccountNotFoundException;
import com.nadson.myfinance.domain.exception.BusinessRuleException;
import com.nadson.myfinance.domain.exception.InvalidTransactionValueException;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    public void execute(UUID senderAccountId, UUID receiverAccountId, BigDecimal amount, LocalDateTime date) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionValueException("Transfer amount must be greater than zero.");
        }

        Account senderAccount = accountRepositoryPort.findById(senderAccountId);
        if (senderAccount == null) {
            throw new AccountNotFoundException(senderAccountId);
        }

        Account receiverAccount = accountRepositoryPort.findById(receiverAccountId);
        if (receiverAccount == null) {
            throw new AccountNotFoundException(receiverAccountId);
        }

        if (senderAccountId.equals(receiverAccountId)) {
            throw new BusinessRuleException("Sender and receiver accounts must be different.");
        }

        senderAccount.withdraw(amount);
        receiverAccount.deposit(amount);

        UUID transferID = UUID.randomUUID();

        Transaction debit = new Transaction(
                UUID.randomUUID(),
                "Transferência enviada para " + receiverAccount.getName(),
                amount,
                date,
                TransactionType.EXPENSE,
                senderAccountId,
                null,
                true,
                transferID,
                null
        );

        Transaction credit = new Transaction(
                UUID.randomUUID(),
                "Transferência recebida de " + senderAccount.getName(),
                amount,
                date,
                TransactionType.INCOME,
                receiverAccountId,
                null,
                true,
                transferID,
                null
        );

        transactionRepositoryPort.save(debit);
        transactionRepositoryPort.save(credit);

        accountRepositoryPort.save(senderAccount);
        accountRepositoryPort.save(receiverAccount);
    }
}