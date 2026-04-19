package com.nadson.myfinance.domain.exception;

public class InvalidTransactionValueException extends RuntimeException {
    public InvalidTransactionValueException(String message) {
        super(message);
    }
}
