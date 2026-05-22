package com.nadson.myfinance.infrastructure.adapter.web.controller;

import com.nadson.myfinance.application.port.in.*;
import com.nadson.myfinance.application.usecase.CategorizeTransactionUseCase;
import com.nadson.myfinance.domain.entity.Budget;
import com.nadson.myfinance.infrastructure.adapter.web.dto.request.BudgetRequest;
import com.nadson.myfinance.infrastructure.adapter.web.dto.response.BudgetResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/budgets")
public class BudgetController {

    private final CreateBudgetPort createBudgetPort;
    private final ListBudgetsPort listBudgetsPort;
    private final UpdateBudgetLimitPort updateBudgetLimitPort;
    private final DeleteBudgetPort deleteBudgetPort;
    private final GetBudgetPort getBudgetPort;

    public BudgetController(CreateBudgetPort createBudgetPort,
                            ListBudgetsPort listBudgetsPort,
                            UpdateBudgetLimitPort updateBudgetLimitPort,
                            DeleteBudgetPort deleteBudgetPort, GetBudgetPort getBudgetPort) {
        this.createBudgetPort = createBudgetPort;
        this.listBudgetsPort = listBudgetsPort;
        this.updateBudgetLimitPort = updateBudgetLimitPort;
        this.deleteBudgetPort = deleteBudgetPort;
        this.getBudgetPort = getBudgetPort;
    }

    @PostMapping
    public ResponseEntity<BudgetResponse> create(@RequestBody BudgetRequest request) {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Budget budget = createBudgetPort.execute(
                UUID.fromString(userId),
                request.categoryId(),
                request.month(),
                request.year(),
                request.limitAmount()
        );
        return ResponseEntity.status(201).body(new BudgetResponse(budget));
    }

    @GetMapping
    public ResponseEntity<List<BudgetResponse>> list(@RequestParam int month, @RequestParam int year) {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Budget> budgets = listBudgetsPort.execute(UUID.fromString(userId), month, year);
        return ResponseEntity.ok(budgets.stream().map(BudgetResponse::new).toList());
    }

    @PatchMapping("/{id}/limit")
    public ResponseEntity<BudgetResponse> updateLimit(@PathVariable UUID id, @RequestBody BigDecimal newLimit) {
        String authenticatedUserId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Budget budget = updateBudgetLimitPort.execute(UUID.fromString(authenticatedUserId), id, newLimit);
        return ResponseEntity.ok(new BudgetResponse(budget));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        String authenticatedUserId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        deleteBudgetPort.execute(UUID.fromString(authenticatedUserId), id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{id}")
    public ResponseEntity<BudgetResponse> getById(@PathVariable UUID id) {
        Budget budget = getBudgetPort.execute(id);
        return ResponseEntity.ok(new BudgetResponse(budget));
    }
}