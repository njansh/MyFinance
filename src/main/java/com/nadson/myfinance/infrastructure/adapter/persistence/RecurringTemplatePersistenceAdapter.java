package com.nadson.myfinance.infrastructure.adapter.persistence;

import com.nadson.myfinance.application.port.out.RecurringTemplateRepositoryPort;
import com.nadson.myfinance.domain.entity.RecurringTemplate;
import com.nadson.myfinance.infrastructure.adapter.persistence.entity.RecurringTemplateJpaEntity;
import com.nadson.myfinance.infrastructure.adapter.persistence.repository.SpringRecurringTemplateRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class RecurringTemplatePersistenceAdapter implements RecurringTemplateRepositoryPort {
    private final SpringRecurringTemplateRepository springRecurringTemplateRepository;

    public RecurringTemplatePersistenceAdapter(SpringRecurringTemplateRepository springRecurringTemplateRepository) {
        this.springRecurringTemplateRepository = springRecurringTemplateRepository;
    }

    @Override
    public RecurringTemplate save(RecurringTemplate recurringTemplate) {
        return springRecurringTemplateRepository.save(RecurringTemplateJpaEntity.fromDomain(recurringTemplate)).toDomain();
    }

    @Override
    public RecurringTemplate findById(UUID id) {
        return springRecurringTemplateRepository.findById(id).map(RecurringTemplateJpaEntity::toDomain).orElse(null);
    }

    @Override
    public List<RecurringTemplate> findPendingTemplates(UUID userId, int currentDay, int currentMonth, int currentYear) {
        return springRecurringTemplateRepository.findPendingTemplates(userId, currentDay, currentMonth, currentYear)
                .stream()
                .map(RecurringTemplateJpaEntity::toDomain)
                .toList();
    }

    @Override
    public void deleteAllByUserId(UUID userId) {
        springRecurringTemplateRepository.deleteAllByUserId(userId);
    }

    @Override
    public void deleteAllByAccountId(UUID accountId) {
springRecurringTemplateRepository.deleteAllByAccountId(accountId);
    }
}
