package com.nadson.myfinance.infrastructure.adapter.web.controller;

import com.nadson.myfinance.application.port.in.*;
import com.nadson.myfinance.domain.entity.User;
import com.nadson.myfinance.infrastructure.adapter.web.dto.request.UserRequest;
import com.nadson.myfinance.infrastructure.adapter.web.dto.response.AccountResponse;
import com.nadson.myfinance.infrastructure.adapter.web.dto.response.CategoryResponse;
import com.nadson.myfinance.infrastructure.adapter.web.dto.response.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {
    private final CreateUserPort createUserPort;
    private final GetUserPort getUserPort;
    private final GetTotalBalancePort getTotalBalancePort;
    private final ListAccountsByUserPort listAccountsByUserPort;

    public UserController(CreateUserPort createUserPort, GetUserPort getUserPort, GetTotalBalancePort getTotalBalancePort, ListAccountsByUserPort listAccountsByUserPort) {
        this.createUserPort = createUserPort;
        this.getUserPort = getUserPort;
        this.getTotalBalancePort = getTotalBalancePort;
        this.listAccountsByUserPort = listAccountsByUserPort;
    }
    
    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request) {
        User createdUser = createUserPort.execute( request.name(), request.email());
        return ResponseEntity.status(201).body(UserResponse.fromDomain(createdUser));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable UUID id) {
        User user = getUserPort.execute(id);
        return ResponseEntity.ok(UserResponse.fromDomain(user));
    }

    @GetMapping("/{id}/total-balance")
    public ResponseEntity<BigDecimal> getTotalBalance(@PathVariable UUID id) {
        return ResponseEntity.ok(getTotalBalancePort.execute(id));
    }


    @GetMapping("/{id}/accounts")
    public ResponseEntity<List<AccountResponse>> getAccountsByUserId(@PathVariable UUID id) {
        var accounts = listAccountsByUserPort.execute(id);
        return ResponseEntity.ok(accounts.stream()
                .map(AccountResponse::fromDomain)
                .toList());
    }
    }

