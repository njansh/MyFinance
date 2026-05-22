package com.nadson.myfinance.infrastructure.adapter.web.controller;

import com.nadson.myfinance.application.port.in.*;
import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.domain.enums.TransactionStatus;
import com.nadson.myfinance.infrastructure.adapter.web.dto.request.TransactionRequest;
import com.nadson.myfinance.infrastructure.adapter.web.dto.request.TransferRequest;
import com.nadson.myfinance.infrastructure.adapter.web.dto.request.UpdateTransactionRequest;
import com.nadson.myfinance.infrastructure.adapter.web.dto.response.BalanceResponse;
import com.nadson.myfinance.infrastructure.adapter.web.dto.response.TransactionResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final CreateTransactionPort createTransactionPort;
    private final GetTransactionPort getTransactionPort;
    private final GetExpensesByCategoryPort getExpensesByCategoryPort;
    private final UpdateTransactionPort updateTransactionPort;
    private final DeleteTransactionPort deleteTransactionPort;
    private final GetAccountBalancePort getAccountBalancePort;
    private final GetIncomesByCategoryPort getIncomesByCategoryPort;
    private final TransferPort transferPort;
    private final ConfirmRecurringPort confirmRecurringPort;

    public TransactionController(CreateTransactionPort createTransactionPort,
                                 GetTransactionPort getTransactionPort,
                                 GetExpensesByCategoryPort getExpensesByCategoryPort,
                                 UpdateTransactionPort updateTransactionPort,
                                 DeleteTransactionPort deleteTransactionPort,
                                 GetAccountBalancePort getAccountBalancePort,
                                 GetIncomesByCategoryPort getIncomesByCategoryPort,
                                 TransferPort transferPort,
                                 ConfirmRecurringPort confirmRecurringPort) {
        this.createTransactionPort = createTransactionPort;
        this.getTransactionPort = getTransactionPort;
        this.getExpensesByCategoryPort = getExpensesByCategoryPort;
        this.updateTransactionPort = updateTransactionPort;
        this.deleteTransactionPort = deleteTransactionPort;
        this.getAccountBalancePort = getAccountBalancePort;
        this.getIncomesByCategoryPort = getIncomesByCategoryPort;
        this.transferPort = transferPort;
        this.confirmRecurringPort = confirmRecurringPort;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> create(@Valid @RequestBody TransactionRequest request) {
        Transaction transaction = new Transaction(
                UUID.randomUUID(), request.description(), request.amount(), request.date(),
                request.type(), request.accountId(), request.categoryId(),
                request.isTransfer(), request.transferID(), request.accountBalanceAfter(),
                TransactionStatus.COMPLETED, null
        );

        var result = createTransactionPort.execute(transaction);

        return ResponseEntity.status(201).body(TransactionResponse.fromDomain(result.transaction(), result.alert()));
    }
    @PostMapping("/transfer")
    public ResponseEntity<Void> transfer(@Valid @RequestBody TransferRequest request) {
        // Adicione este log para ver o que o Spring recebeu
        System.out.println("Recebido descrição: " + request.description());

        transferPort.execute(
                request.fromId(),
                request.toId(),
                request.amount(),
                request.date(),
                request.description(),
                null,
                null
        );
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/{transactionId}/confirm")
    public ResponseEntity<TransactionResponse> confirmTransaction(
            @PathVariable UUID transactionId,
            @RequestParam BigDecimal actualAmount,
            @RequestParam(required = false) LocalDateTime actualDate) {

        String authenticatedUserId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LocalDateTime dateToSave = actualDate != null ? actualDate : LocalDateTime.now();

        Transaction confirmedTransaction = confirmRecurringPort.execute(
                UUID.fromString(authenticatedUserId), transactionId, actualAmount, dateToSave
        );
        return ResponseEntity.ok(TransactionResponse.fromDomain(confirmedTransaction, null));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> update(@PathVariable UUID id, @Valid @RequestBody UpdateTransactionRequest request) {
        var result = updateTransactionPort.execute(
                id, request.description(), request.amount(), request.date(),
                request.type(), request.accountId(), request.categoryId()
        );

       return ResponseEntity.ok(TransactionResponse.fromDomain(result.transaction(), result.alert()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getById(@PathVariable UUID id) {
        Transaction transaction = getTransactionPort.execute(id);
        return ResponseEntity.ok(TransactionResponse.fromDomain(transaction, null));
    }

    @GetMapping("/reports/balance/{accountId}")
    public ResponseEntity<BalanceResponse> getBalance(
            @PathVariable UUID accountId,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;

        if (month!= null && year!= null) {
            YearMonth yearMonth = YearMonth.of(year, month);
            startDate = yearMonth.atDay(1).atStartOfDay();
            endDate = yearMonth.atEndOfMonth().atTime(23, 59, 59);
        }
        return ResponseEntity.ok(getAccountBalancePort.execute(accountId, startDate, endDate));
    }

    @GetMapping("/reports/expenses-by-category/{accountId}")
    public ResponseEntity<Map<String, BigDecimal>> getExpensesReport(
            @PathVariable UUID accountId,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;

        if (month!= null && year!= null) {
            YearMonth yearMonth = YearMonth.of(year, month);
            startDate = yearMonth.atDay(1).atStartOfDay();
            endDate = yearMonth.atEndOfMonth().atTime(23, 59, 59);
        }
        Map<String, BigDecimal> report = getExpensesByCategoryPort.execute(accountId, startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/reports/incomes-by-category/{accountId}")
    public ResponseEntity<Map<String, BigDecimal>> getIncomesReport(
            @PathVariable UUID accountId,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;

        if (month!= null && year!= null) {
            YearMonth yearMonth = YearMonth.of(year, month);
            startDate = yearMonth.atDay(1).atStartOfDay();
            endDate = yearMonth.atEndOfMonth().atTime(23, 59, 59);
        }
        Map<String, BigDecimal> report = getIncomesByCategoryPort.execute(accountId, startDate, endDate);
        return ResponseEntity.ok(report);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        deleteTransactionPort.execute(id);
        return ResponseEntity.ok().build();
    }
}