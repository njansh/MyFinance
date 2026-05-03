package com.nadson.myfinance.application.listener;

import com.nadson.myfinance.application.port.out.BudgetRepositoryPort;
import com.nadson.myfinance.domain.entity.Budget;
import com.nadson.myfinance.domain.event.TransactionCreatedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class BudgetAlertEventListener {

    private final BudgetRepositoryPort budgetRepository;

    public BudgetAlertEventListener(BudgetRepositoryPort budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTransactionCreated(TransactionCreatedEvent event) {
        if (event.categoryId() == null) return;

        Budget budget = budgetRepository.findByUserIdAndCategoryIdAndMonthAndYear(
                event.userId(), event.categoryId(), event.month(), event.year()
        );

        if (budget!= null) {
            budget.addExpense(event.amount());
            budgetRepository.save(budget);

            if (budget.isExceeded()) {
                // Futuramente podemos enviar um e-mail, notificação push, etc.
                System.err.println("ALERTA VERMELHO: O orçamento para esta categoria foi estourado!");
            } else if (budget.isNearingLimit()) {
                System.out.println("ALERTA AMARELO: Você já gastou 80% ou mais do seu orçamento nesta categoria!");
            }
        }
    }
}