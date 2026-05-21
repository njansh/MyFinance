package com.nadson.myfinance.application.port.in;

import com.nadson.myfinance.domain.entity.RecurringTemplate;
import java.util.List;
import java.util.UUID;

public interface ListRecurringTemplatesPort {
    List<RecurringTemplate> execute(UUID userId);
}
