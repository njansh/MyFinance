package com.nadson.myfinance.application.port.in;
import java.util.UUID;

public interface DeleteUserPort {
    void execute(UUID userId);
}