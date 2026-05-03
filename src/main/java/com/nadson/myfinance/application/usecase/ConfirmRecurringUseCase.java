package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.ConfirmRecurringPort;
import com.nadson.myfinance.application.port.in.CreateTransactionPort;
import com.nadson.myfinance.application.port.out.RecurringTemplateRepositoryPort;
import com.nadson.myfinance.domain.entity.RecurringTemplate;
import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.domain.exception.BusinessRuleException;
import com.nadson.myfinance.domain.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class ConfirmRecurringUseCase implements ConfirmRecurringPort {
    private final RecurringTemplateRepositoryPort repository;
    private final CreateTransactionPort createTransactionPort;

    public ConfirmRecurringUseCase(RecurringTemplateRepositoryPort repository, CreateTransactionPort createTransactionPort) {
        this.repository = repository;
        this.createTransactionPort = createTransactionPort;
    }

    @Transactional
    @Override
    public Transaction execute(UUID userId, UUID templateId, BigDecimal actualAmount, LocalDateTime actualDate) {
        RecurringTemplate template = repository.findById(templateId);
        if (template == null) throw new ResourceNotFoundException("Modelo não encontrado");

        // Regra de Isolamento: Verifica se o template pertence ao usuário logado
        if (!template.getUserId().equals(userId)) {
            throw new BusinessRuleException("Acesso negado: Você não tem permissão para alterar este recurso.");
        }

        // 1. Cria a transação REAL
        Transaction realTransaction = new Transaction(
                UUID.randomUUID(),
                template.getDescription(),
                actualAmount,
                actualDate,
                template.getType(),
                template.getAccountId(),
                template.getCategoryId(),
                false, null, null
        );

        // 2. Chama a porta de criação de transação (que já atualiza saldo atomicamente)
        createTransactionPort.execute(realTransaction);

        // 3. Atualiza o modelo para saber que este mês já foi processado
        template.setLastExecution(actualDate.getMonthValue(), actualDate.getYear());
        repository.save(template);

        return realTransaction;
    }
}