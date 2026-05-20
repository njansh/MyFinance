package com.nadson.myfinance.domain.exception;

import java.util.UUID;

public class CreditCardNotFoundException extends ResourceNotFoundException {
    public CreditCardNotFoundException(UUID id) {
        super("Credit card with ID " + id + " was not found.");
    }
}
