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

        List<RecurringTemplate> templatesParaGerar = recurringTemplateRepositoryPort.findPendingTemplates(userId, targetMonth, targetYear);

        for (RecurringTemplate template : templatesParaGerar) {
            gerarFaturasPendentes(template, targetMonth, targetYear);
            recurringTemplateRepositoryPort.save(template);
        }

        return transactionRepositoryPort.findAllPendingByUserId(userId);
    }

    private void gerarFaturasPendentes(RecurringTemplate template, int targetMonth, int targetYear) {
        int startMonth = template.getLastExecutedMonth() != null ? template.getLastExecutedMonth() + 1 : targetMonth;
        int startYear = template.getLastExecutedYear() != null ? template.getLastExecutedYear() : targetYear;

        if (startMonth > 12) {
            startMonth = 1;
            startYear++;
        }

        LocalDate current = LocalDate.of(startYear, startMonth, 1);
        LocalDate target = LocalDate.of(targetYear, targetMonth, 1);

        while (!current.isAfter(target)) {
            // AQUI ESTÁ A CORREÇÃO: O TransactionStatus.PENDING agora é o 11º parâmetro, não tem erro!
            Transaction faturaPendente = new Transaction(
                    UUID.randomUUID(),
                    template.getDescription() + " (" + current.getMonthValue() + "/" + current.getYear() + ")",
                    template.getExpectedAmount(),
                    LocalDateTime.of(current.getYear(), current.getMonthValue(), template.getFrequencyDay(), 0, 0),
                    template.getType(),
                    template.getAccountId(),
                    template.getCategoryId(),
                    false,
                    null,
                    null,
                    TransactionStatus.PENDING
            );

            transactionRepositoryPort.save(faturaPendente);

            template.setLastExecution(current.getMonthValue(), current.getYear());

            current = current.plusMonths(1);
        }
    }
}