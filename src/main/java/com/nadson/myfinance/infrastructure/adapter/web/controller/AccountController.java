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
    }
    @GetMapping("/{id}/transactions")
    public ResponseEntity<List<TransactionResponse>> getTransactions(@PathVariable UUID id) {
        List<Transaction> transactions = listTransactionsPort.execute(id);
        return ResponseEntity.ok(transactions.stream()
                .map(TransactionResponse::fromDomain)
                .toList());
    }
}
