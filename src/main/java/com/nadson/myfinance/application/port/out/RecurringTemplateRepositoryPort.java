package com.nadson.myfinance.application.port.out;

import com.nadson.myfinance.domain.entity.RecurringTemplate;

import java.util.List;
import java.util.UUID;

public interface RecurringTemplateRepositoryPort {
    RecurringTemplate save(RecurringTemplate recurringTemplate);
    RecurringTemplate findById(UUID id);
    List<RecurringTemplate> findPendingTemplates(UUID userId, int currentDay, int currentMonth, int currentYear);

    void deleteAllByUserId(UUID userId);

    void deleteAllByAccountId(UUID accountId);
}
