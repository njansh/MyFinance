package com.nadson.myfinance.application.port.in;

import java.util.UUID;

public interface DeleteRecurringTemplatePort {
    void execute(UUID userId, UUID templateId);
}
