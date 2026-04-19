package com.nadson.myfinance.domain.exception;

import java.util.UUID;

public class TransactionNotFoundException extends ResourceNotFoundException {
    public TransactionNotFoundException(UUID id) {
        super("Transaction with ID " + id + " was not found.");
    }
}