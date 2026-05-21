package com.nadson.myfinance.application.port.in;

import com.nadson.myfinance.domain.entity.RecurringTemplate;

public interface CreateRecurringTemplatePort {
    RecurringTemplate execute(RecurringTemplate template);
}