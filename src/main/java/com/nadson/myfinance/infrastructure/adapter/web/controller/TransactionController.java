package com.nadson.myfinance.infrastructure.adapter.web.controller;

import com.nadson.myfinance.application.port.in.*;
import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.domain.exception.DuplicateResourceException;
import com.nadson.myfinance.infrastructure.adapter.persistence.repository.SpringIdempotencyRepository;
import com.nadson.myfinance.infrastructure.adapter.web.dto.request.TransactionRequest;
import com.nadson.myfinance.infrastructure.adapter.web.dto.request.UpdateTransactionRequest;
import com.nadson.myfinance.infrastructure.adapter.web.dto.response.BalanceResponse;
import com.nadson.myfinance.infrastructure.adapter.web.dto.response.TransactionResponse;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    private final SpringIdempotencyRepository idempotencyRepository;

    public TransactionController(CreateTransactionPort createTransactionPort, GetTransactionPort getTransactionPort, GetExpensesByCategoryPort getExpensesByCategoryPort, UpdateTransactionPort updateTransactionPort, DeleteTransactionPort deleteTransactionPort, GetAccountBalancePort getAccountBalancePort, GetIncomesByCategoryPort getIncomesByCategoryPort, SpringIdempotencyRepository idempotencyRepository) {
        this.createTransactionPort = createTransactionPort;
        this.getTransactionPort = getTransactionPort;
        this.getExpensesByCategoryPort = getExpensesByCategoryPort;
        this.updateTransactionPort = updateTransactionPort;
        this.deleteTransactionPort = deleteTransactionPort;
        this.getAccountBalancePort = getAccountBalancePort;
        this.getIncomesByCategoryPort = getIncomesByCategoryPort;
        this.idempotencyRepository = idempotencyRepository;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> create(
            @RequestHeader(value = "Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody TransactionRequest request) {

        // Tentativa de registro da chave de idempotência (Blind Insert)
        try {
            idempotencyRepository.insertKey(idempotencyKey);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateResourceException("Uma transação idêntica já foi processada para esta chave de segurança.");
        }

        Transaction transaction = new Transaction(
                UUID.randomUUID(),
                request.description(),
                request.amount(),
                request.date(),
                request.type(),
                request.accountId(),
                request.categoryId(),
                request.isTransfer(),
                request.transferID(),
                request.accountBalanceAfter()
        );
        Transaction createdTransaction = createTransactionPort.execute(transaction);
        return ResponseEntity.status(201).body(TransactionResponse.fromDomain(createdTransaction));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable UUID id, @Valid @RequestBody UpdateTransactionRequest request) {
        updateTransactionPort.execute(
                id,
                request.description(),
                request.amount(),
                request.date(),
                request.type(),
                request.accountId(),
                request.categoryId()
        );
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getById(@PathVariable UUID id) {
        Transaction transaction = getTransactionPort.execute(id);
        return ResponseEntity.ok(TransactionResponse.fromDomain(transaction));
    }
    @GetMapping("/reports/balance/{accountId}")
    public ResponseEntity<BalanceResponse> getBalance(
            @PathVariable UUID accountId,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {

        LocalDateTime startDate = null;
        LocalDateTime endDate = null;

        if (month != null && year != null) {
            YearMonth yearMonth = YearMonth.of(year, month);
            startDate = yearMonth.atDay(1).atStartOfDay();
            endDate = yearMonth.atEndOfMonth().atTime(23, 59, 59);
        }

        return ResponseEntity.ok(getAccountBalancePort.execute(accountId, startDate, endDate));
    }

    @GetMapping("/reports/expenses-by-category/{accountId}")
    public ResponseEntity<Map<String, BigDecimal>> getExpensesReport
            (@PathVariable UUID accountId,
             @RequestParam(required = false) Integer month,
             @RequestParam(required = false) Integer year) {
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;
        if (month != null && year != null) {
            YearMonth yearMonth = YearMonth.of(year, month);
            startDate = yearMonth.atDay(1).atStartOfDay();
            endDate = yearMonth.atEndOfMonth().atTime(23, 59, 59);
        }
        Map<String, BigDecimal> report = getExpensesByCategoryPort.execute(accountId, startDate, endDate);
        return ResponseEntity.ok(report);
    }
    @GetMapping("/reports/incomes-by-category/{accountId}")
    public ResponseEntity<Map<String, BigDecimal>> getIncomesReport
            (@PathVariable UUID accountId,
             @RequestParam(required = false) Integer month,
             @RequestParam(required = false) Integer year) {
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;
        if (month != null && year != null) {
            YearMonth yearMonth = YearMonth.of(year, month);
            startDate = yearMonth.atDay(1).atStartOfDay();
            endDate = yearMonth.atEndOfMonth().atTime(23, 59, 59);
        }
        Map<String, BigDecimal> report = getIncomesByCategoryPort.execute(accountId, startDate, endDate);
        return ResponseEntity.ok(report);
    }


    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        deleteTransactionPort.execute(id);
        return ResponseEntity.ok().build();
    }
}
