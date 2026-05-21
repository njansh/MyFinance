package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.ListPendingRecurringPort;
import com.nadson.myfinance.application.port.out.RecurringTemplateRepositoryPort;
import com.nadson.myfinance.application.port.out.TransactionRepositoryPort;
import com.nadson.myfinance.application.port.out.UserRepositoryPort;
import com.nadson.myfinance.domain.entity.RecurringTemplate;
import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.domain.enums.TransactionStatus;
import com.nadson.myfinance.domain.exception.BusinessRuleException;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

public class ListPendingRecurringUseCase implements ListPendingRecurringPort {
    private final UserRepositoryPort userRepositoryPort;
    private final RecurringTemplateRepositoryPort recurringTemplateRepositoryPort;
    private final TransactionRepositoryPort transactionRepositoryPort;

    public ListPendingRecurringUseCase(UserRepositoryPort userRepositoryPort,
                                       RecurringTemplateRepositoryPort recurringTemplateRepositoryPort,
                                       TransactionRepositoryPort transactionRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
        this.recurringTemplateRepositoryPort = recurringTemplateRepositoryPort;
        this.transactionRepositoryPort = transactionRepositoryPort;
    }

    @Override
    @Transactional
    public List<Transaction> execute(UUID userId, int targetMonth, int targetYear) {
        if (userRepositoryPort.findById(userId) == null) {
            throw new BusinessRuleException("User not found");
        }

        List<RecurringTemplate> pendingTemplates = recurringTemplateRepositoryPort.findPendingTemplates(userId, targetMonth, targetYear);

        for (RecurringTemplate template : pendingTemplates) {
            generatePendingTransactions(template, targetMonth, targetYear);
            recurringTemplateRepositoryPort.save(template);
        }

        LocalDateTime start = LocalDateTime.of(2000, 1, 1, 0, 0);
        LocalDateTime end = YearMonth.of(targetYear, targetMonth).atEndOfMonth().atTime(23, 59, 59);

        return transactionRepositoryPort.findAllPendingByUserIdUpToDate(userId, start, end);
    }

    private void generatePendingTransactions(RecurringTemplate template, int targetMonth, int targetYear) {
        int startMonth = template.getLastExecutedMonth() != null ? template.getLastExecutedMonth() + 1 : targetMonth;
        int startYear = template.getLastExecutedYear() != null ? template.getLastExecutedYear() : targetYear;

        if (startMonth > 12) {
            startMonth = 1;
            startYear++;
        }

        LocalDate current = LocalDate.of(startYear, startMonth, 1);
        LocalDate target = LocalDate.of(targetYear, targetMonth, 1);

        while (!current.isAfter(target)) {
            int maxDaysInMonth = YearMonth.of(current.getYear(), current.getMonthValue()).lengthOfMonth();
            int safeDay = Math.min(template.getFrequencyDay(), maxDaysInMonth);

            Transaction pendingTransaction = new Transaction(
                    UUID.randomUUID(),
                    template.getDescription() + " (" + current.getMonthValue() + "/" + current.getYear() + ")",
                    template.getExpectedAmount(),
                    LocalDateTime.of(current.getYear(), current.getMonthValue(), safeDay, 0, 0),
                    template.getType(),
                    template.getAccountId(),
                    template.getCategoryId(),
                    false,
                    null,
                    null,
                    TransactionStatus.PENDING
            );

            transactionRepositoryPort.save(pendingTransaction);

            template.setLastExecution(current.getMonthValue(), current.getYear());

            current = current.plusMonths(1);
        }
    }
}
