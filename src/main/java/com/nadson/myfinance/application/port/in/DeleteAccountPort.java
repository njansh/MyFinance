package com.nadson.myfinance.application.port.in;
import java.util.UUID;

public interface DeleteAccountPort {
    void execute(UUID accountId, UUID userId);
}