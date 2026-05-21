package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.DeleteRecurringTemplatePort;
import com.nadson.myfinance.application.port.out.RecurringTemplateRepositoryPort;
import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
import com.nadson.myfinance.domain.entity.RecurringTemplate;
import com.nadson.myfinance.domain.exception.BusinessRuleException;
import com.nadson.myfinance.domain.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;

import java.util.UUID;

public class DeleteRecurringTemplateUseCase implements DeleteRecurringTemplatePort {
    private final RecurringTemplateRepositoryPort repository;
    private final TransactionRepositoryPort transactionRepositoryPort;

    public DeleteRecurringTemplateUseCase(RecurringTemplateRepositoryPort repository, TransactionRepositoryPort transactionRepositoryPort) {
        this.repository = repository;
        this.transactionRepositoryPort = transactionRepositoryPort;
    }

    @Transactional
    @Override
    public void execute(UUID userId, UUID templateId) {
        RecurringTemplate template = repository.findById(templateId);

        if (template == null) {
            throw new ResourceNotFoundException("Recurring template not found");
        }

        if (!template.getUserId().equals(userId)) {
            throw new BusinessRuleException("Access denied: You cannot delete this template");
        }


        transactionRepositoryPort.deletePendingByTemplateId(templateId);

        repository.deleteById(templateId);
    }
}
