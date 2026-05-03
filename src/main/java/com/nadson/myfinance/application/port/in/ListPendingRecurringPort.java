package com.nadson.myfinance.application.port.in;

import com.nadson.myfinance.domain.entity.RecurringTemplate;

import java.util.List;
import java.util.UUID;

public interface ListPendingRecurringPort {
    List<RecurringTemplate> execute(UUID userId);
}
