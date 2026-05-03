package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.ListPendingRecurringPort;
import com.nadson.myfinance.application.port.out.AccountRepositoryPort;
import com.nadson.myfinance.application.port.out.RecurringTemplateRepositoryPort;
import com.nadson.myfinance.application.port.out.UserRepositoryPort;
import com.nadson.myfinance.domain.entity.RecurringTemplate;
import com.nadson.myfinance.domain.exception.BusinessRuleException;

import java.util.List;
import java.util.UUID;

public class ListPendingRecurringUseCase implements ListPendingRecurringPort
{
    private final UserRepositoryPort userRepositoryPort;
    private final RecurringTemplateRepositoryPort recurringTemplateRepositoryPort;

    public ListPendingRecurringUseCase(UserRepositoryPort userRepositoryPort, RecurringTemplateRepositoryPort recurringTemplateRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
        this.recurringTemplateRepositoryPort = recurringTemplateRepositoryPort;
    }

    @Override
    public List<RecurringTemplate> execute(UUID userId) {
        if (userRepositoryPort.findById(userId) == null) {
            throw new BusinessRuleException("User not found");
        }

        java.time.LocalDate now = java.time.LocalDate.now();
        int currentDay = now.getDayOfMonth();
        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear();

        return recurringTemplateRepositoryPort.findPendingTemplates(userId, currentDay, currentMonth, currentYear);
    }
}
