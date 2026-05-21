package com.nadson.myfinance.infrastructure.adapter.web.controller;

import com.nadson.myfinance.application.port.in.*;
import com.nadson.myfinance.application.port.out.BillingPaymentRepositoryPort;
import com.nadson.myfinance.application.port.out.RecurringTemplateRepositoryPort;
import com.nadson.myfinance.domain.entity.RecurringTemplate;
import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.infrastructure.adapter.web.dto.request.CreateRecurringTemplateRequest;
import com.nadson.myfinance.infrastructure.adapter.web.dto.response.TransactionResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/recurring")
public class RecurringController {

    private final ListPendingRecurringPort listPendingRecurringPort;
    private final CreateRecurringTemplatePort createRecurringTemplatePort;
    private final ListRecurringTemplatesPort listRecurringTemplatesPort;
    private final DeleteRecurringTemplatePort deleteRecurringTemplatePort;

    public RecurringController(ListPendingRecurringPort listPendingRecurringPort, CreateRecurringTemplatePort createRecurringTemplatePort, ListRecurringTemplatesPort listRecurringTemplatesPort, DeleteRecurringTemplatePort deleteRecurringTemplatePort) {
        this.listPendingRecurringPort = listPendingRecurringPort;
        this.createRecurringTemplatePort = createRecurringTemplatePort;
        this.listRecurringTemplatesPort = listRecurringTemplatesPort;
        this.deleteRecurringTemplatePort = deleteRecurringTemplatePort;
    }
    @PostMapping
    public ResponseEntity<RecurringTemplate> createTemplate(@RequestBody @Valid CreateRecurringTemplateRequest request) {
        String authenticatedUserId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        RecurringTemplate newTemplate = new RecurringTemplate(
                UUID.randomUUID(),
                UUID.fromString(authenticatedUserId),
                request.accountId(),
                request.categoryId(),
                request.description(),
                request.expectedAmount(),
                request.type(),
                request.frequencyDay(),
                true
        );

        RecurringTemplate savedTemplate = createRecurringTemplatePort.execute(newTemplate);

        return ResponseEntity.status(201).body(savedTemplate);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<TransactionResponse>> getPendingTransactions(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {

        String authenticatedUserId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        int targetMonth = (month != null) ? month : java.time.LocalDate.now().getMonthValue();
        int targetYear = (year != null) ? year : java.time.LocalDate.now().getYear();

        List<Transaction> pendingTransactions = listPendingRecurringPort.execute(
                UUID.fromString(authenticatedUserId), targetMonth, targetYear
        );

        List<TransactionResponse> response = pendingTransactions.stream()
                .map(TransactionResponse::fromDomain)
                .toList();

        return ResponseEntity.ok(response);
    }
    @GetMapping
    public ResponseEntity<List<RecurringTemplate>> listTemplates() {
        String authenticatedUserId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<RecurringTemplate> templates = listRecurringTemplatesPort.execute(UUID.fromString(authenticatedUserId));
        return ResponseEntity.ok(templates);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable UUID id) {
        String authenticatedUserId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        deleteRecurringTemplatePort.execute(UUID.fromString(authenticatedUserId), id);
        return ResponseEntity.noContent().build();
    }
}