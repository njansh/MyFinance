package com.nadson.myfinance.domain.exception;

import java.util.UUID;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(UUID id) {
        super("Account with ID " + id + " was not found.");
    }
}