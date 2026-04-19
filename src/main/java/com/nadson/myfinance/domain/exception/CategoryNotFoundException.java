package com.nadson.myfinance.domain.exception;

import java.util.UUID;

public class CategoryNotFoundException extends ResourceNotFoundException {
    public CategoryNotFoundException(UUID id) {
        super("Category with ID " + id + " was not found.");
    }
}