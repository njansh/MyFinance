package com.nadson.myfinance.application.port.in;

import com.nadson.myfinance.domain.entity.Transaction;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public interface ConfirmRecurringPort {
    Transaction execute(UUID userId, UUID transactionId, BigDecimal actualAmount, LocalDateTime actualDate);
}