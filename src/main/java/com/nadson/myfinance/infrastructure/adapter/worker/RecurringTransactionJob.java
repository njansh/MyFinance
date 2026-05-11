package com.nadson.myfinance.infrastructure.adapter.worker;

import com.nadson.myfinance.application.port.in.ConfirmRecurringPort;
import com.nadson.myfinance.infrastructure.adapter.persistence.entity.RecurringTemplateJpaEntity;
import com.nadson.myfinance.infrastructure.adapter.persistence.repository.SpringRecurringTemplateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class RecurringTransactionJob {

    private static final Logger log = LoggerFactory.getLogger(RecurringTransactionJob.class);

    private final SpringRecurringTemplateRepository repository;
    private final ConfirmRecurringPort confirmRecurringPort;

    public RecurringTransactionJob(SpringRecurringTemplateRepository repository, ConfirmRecurringPort confirmRecurringPort) {
        this.repository = repository;
        this.confirmRecurringPort = confirmRecurringPort;
    }

    @Scheduled(cron = "0 0 2 * * *")
    public void processPendingRecurringTransactions() {
        log.info("Starting recurring transaction processing routine...");
        try {
            LocalDate today = LocalDate.now();
            List<RecurringTemplateJpaEntity> templates = repository.findAll();

            for (RecurringTemplateJpaEntity template : templates) {
                boolean isDue = template.isActive() &&
                        template.getFrequencyDay() <= today.getDayOfMonth() &&
                        (template.getLastExecutedMonth() == null ||
                                template.getLastExecutedYear() < today.getYear() ||
                                (template.getLastExecutedYear() == today.getYear() && template.getLastExecutedMonth() < today.getMonthValue()));

                if (isDue) {
                    try {
                        confirmRecurringPort.execute(
                                template.getUserId(),
                                template.getId(),
                                template.getExpectedAmount(),
                                LocalDateTime.now()
                        );
                        log.info("Successfully processed recurrence: " + template.getDescription());
                    } catch (Exception e) {
                        log.error("Failed to trigger recurrence ID: " + template.getId(), e);
                    }
                }
            }
            log.info("Recurring transaction routine finished.");
        } catch (Exception e) {
            log.error("Critical error processing background transactions", e);
        }
    }
}
