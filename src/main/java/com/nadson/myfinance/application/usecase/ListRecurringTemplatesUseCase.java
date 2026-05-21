package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.ListRecurringTemplatesPort;
import com.nadson.myfinance.application.port.out.RecurringTemplateRepositoryPort;
import com.nadson.myfinance.domain.entity.RecurringTemplate;
import java.util.List;
import java.util.UUID;

public class ListRecurringTemplatesUseCase implements ListRecurringTemplatesPort {

    private final RecurringTemplateRepositoryPort repository;

    public ListRecurringTemplatesUseCase(RecurringTemplateRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public List<RecurringTemplate> execute(UUID userId) {
        return repository.findAllByUserId(userId);
    }
}