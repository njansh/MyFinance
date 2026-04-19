package com.nadson.myfinance.domain.exception;

import java.util.UUID;

public class UserNotFoundException extends ResourceNotFoundException {
    public UserNotFoundException(UUID id) {
        super("User with ID " + id + " was not found.");
    }
}