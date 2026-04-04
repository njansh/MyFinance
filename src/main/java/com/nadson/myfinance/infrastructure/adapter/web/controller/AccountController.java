package com.nadson.myfinance.infrastructure.adapter.web.controller;

import com.nadson.myfinance.application.port.in.CreateAccountPort;
import com.nadson.myfinance.application.port.in.GetAccountport;
import com.nadson.myfinance.domain.entity.Account;
import com.nadson.myfinance.infrastructure.adapter.web.dto.response.AccountResponse;
import com.nadson.myfinance.infrastructure.adapter.web.dto.response.CreateAccountRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    private final GetAccountport getAccountport;
    private final CreateAccountPort createAccountPort;

    public AccountController(GetAccountport getAccountport, CreateAccountPort createAccountPort) {
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
                com.nadson.myfinance.domain.enums.AccountType.valueOf(request.getType().toUpperCase())
        );

        return ResponseEntity.status(201).body(AccountResponse.fromDomain(account));
    }
}
