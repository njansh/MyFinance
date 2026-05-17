package com.nadson.myfinance.infrastructure.adapter.web.controller;

import com.nadson.myfinance.application.port.in.*;
import com.nadson.myfinance.application.usecase.ListTransactionsUseCase;
import com.nadson.myfinance.domain.entity.User;
import com.nadson.myfinance.infrastructure.adapter.web.dto.request.UserRequest;
import com.nadson.myfinance.infrastructure.adapter.web.dto.response.AccountResponse;
import com.nadson.myfinance.infrastructure.adapter.web.dto.response.CategoryResponse;
import com.nadson.myfinance.infrastructure.adapter.web.dto.response.TransactionResponse;
import com.nadson.myfinance.infrastructure.adapter.web.dto.response.UserResponse;
import com.nadson.myfinance.infrastructure.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final GetCategoriesPort getCategoriesPort;
    private final JwtService jwtService;
    private final DeleteUserPort deleteUserPort;
    private final ListTransactionsUseCase listTransactionsUseCase;

    public UserController(CreateUserPort createUserPort,
                          GetUserPort getUserPort,
                          GetTotalBalancePort getTotalBalancePort,
                          ListAccountsByUserPort listAccountsByUserPort,
                          GetCategoriesPort getCategoriesPort,
                          JwtService jwtService,
                          DeleteUserPort deleteUserPort, ListTransactionsUseCase listTransactionsUseCase) {
        this.createUserPort = createUserPort;
        this.getUserPort = getUserPort;
        this.getTotalBalancePort = getTotalBalancePort;
        this.listAccountsByUserPort = listAccountsByUserPort;
        this.getCategoriesPort = getCategoriesPort;
        this.jwtService = jwtService;
        this.deleteUserPort = deleteUserPort;
        this.listTransactionsUseCase = listTransactionsUseCase;
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request) {
        User createdUser = createUserPort.execute(request.name(), request.email(), request.password());
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

    @GetMapping("/{id}/categories")
    public ResponseEntity<List<CategoryResponse>> getCategoriesByUserId(@PathVariable UUID id) {
        var categories = getCategoriesPort.execute(id);
        return ResponseEntity.ok(categories.stream()
                .map(CategoryResponse::fromDomain)
                .toList());
    }

    @GetMapping("/{id}/token")
    public ResponseEntity<String> generateDevelopmentToken(@PathVariable UUID id) {
        getUserPort.execute(id);
        return ResponseEntity.ok(jwtService.generateToken(id.toString()));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyUser() {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        deleteUserPort.execute(UUID.fromString(userId));
        return ResponseEntity.noContent().build();
    }
}