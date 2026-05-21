package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.CreateRecurringTemplatePort;
import com.nadson.myfinance.application.port.out.RecurringTemplateRepositoryPort;
import com.nadson.myfinance.domain.entity.RecurringTemplate;
import jakarta.transaction.Transactional;

public class CreateRecurringTemplateUseCase implements CreateRecurringTemplatePort {

    private final RecurringTemplateRepositoryPort repository;

    public CreateRecurringTemplateUseCase(RecurringTemplateRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public RecurringTemplate execute(RecurringTemplate template) {
return repository.save(template);
    }
}