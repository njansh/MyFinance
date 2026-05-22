package com.nadson.myfinance.application.port.in;
import java.util.UUID;

public interface DeleteBudgetPort {
    void execute(UUID userId, UUID budgetId);
}