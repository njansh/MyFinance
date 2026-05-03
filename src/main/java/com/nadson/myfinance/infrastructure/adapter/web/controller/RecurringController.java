package com.nadson.myfinance.infrastructure.adapter.web.controller;

import com.nadson.myfinance.application.port.in.ConfirmRecurringPort;
import com.nadson.myfinance.application.port.in.ListPendingRecurringPort;
import com.nadson.myfinance.domain.entity.RecurringTemplate;
import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.infrastructure.adapter.web.dto.response.TransactionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/recurring")
public class RecurringController {

    private final ListPendingRecurringPort listPendingRecurringPort;
    private final ConfirmRecurringPort confirmRecurringPort;

    public RecurringController(ListPendingRecurringPort listPendingRecurringPort, ConfirmRecurringPort confirmRecurringPort) {
        this.listPendingRecurringPort = listPendingRecurringPort;
        this.confirmRecurringPort = confirmRecurringPort;
    }

    @GetMapping("/pending")
    public ResponseEntity<List<RecurringTemplate>> getPendingTransactions() {
        String authenticatedUserId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<RecurringTemplate> pending = listPendingRecurringPort.execute(UUID.fromString(authenticatedUserId));
        return ResponseEntity.ok(pending);
    }

    @PostMapping("/{templateId}/confirm")
    public ResponseEntity<TransactionResponse> confirmTransaction(
            @PathVariable UUID templateId,
            @RequestParam BigDecimal actualAmount,
            @RequestParam(required = false) LocalDateTime actualDate) {

        // Se a data não for enviada, assume o momento da confirmação
        LocalDateTime dateToSave = actualDate!= null? actualDate : LocalDateTime.now();

        Transaction confirmedTransaction = confirmRecurringPort.execute(templateId, actualAmount, dateToSave);
        return ResponseEntity.ok(TransactionResponse.fromDomain(confirmedTransaction));
    }
}