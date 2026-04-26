package com.nadson.myfinance.application.port.in;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public interface TransferPort {
    void execute(UUID senderAccountId, UUID receiverAccountId, BigDecimal amount, LocalDateTime date);
}
