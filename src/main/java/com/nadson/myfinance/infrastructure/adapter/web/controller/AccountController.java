package com.nadson.myfinance.infrastructure.adapter.web.controller;

import com.nadson.myfinance.application.port.in.CreateAccountPort;
import com.nadson.myfinance.application.port.in.GetAccountport;
import com.nadson.myfinance.application.port.in.ListTransactionsPort;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.domain.enums.AccountType;
import com.nadson.myfinance.infrastructure.adapter.web.dto.response.AccountResponse;
import com.nadson.myfinance.infrastructure.adapter.web.dto.request.CreateAccountRequest;
import com.nadson.myfinance.infrastructure.adapter.web.dto.response.TransactionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    private final ListTransactionsPort listTransactionsPort;
    private final GetAccountport getAccountport;
    private final CreateAccountPort createAccountPort;

    public AccountController(ListTransactionsPort listTransactionsPort, GetAccountport getAccountport, CreateAccountPort createAccountPort) {
        this.listTransactionsPort = listTransactionsPort;
        this.getAccountport = getAccountport;
        this.createAccountPort = createAccountPort;

    }
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getById(@PathVariable UUID id) {
        Account account = getAccountport.execute(id);

        return ResponseEntity.ok(AccountResponse.fromDomain(account));
    }
    @PostMapping
    public ResponseEntity<AccountResponse> create(@RequestBody CreateAccountRequest request) {
        Account account = createAccountPort.execute(
                request.getUserId(),
                request.getName(),
                AccountType.valueOf(request.getType().toUpperCase())
        );

        return ResponseEntity.status(201).body(AccountResponse.fromDomain(account));
    }@GetMapping("/{id}/transactions")
    public ResponseEntity<List<TransactionResponse>> listTransactions(
            @PathVariable UUID id, // <-- Mudei para 'id' para bater com a URL
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {

        LocalDateTime startDate = null;
        LocalDateTime endDate = null;

        if (month != null && year != null) {
            YearMonth yearMonth = YearMonth.of(year, month);
            startDate = yearMonth.atDay(1).atStartOfDay();
            endDate = yearMonth.atEndOfMonth().atTime(23, 59, 59);
        }

        List<Transaction> transactions = listTransactionsPort.execute(id, startDate, endDate);

        List<TransactionResponse> response = transactions.stream()
                .map(TransactionResponse::fromDomain)
                .toList();

        return ResponseEntity.ok(response);
    }
}
