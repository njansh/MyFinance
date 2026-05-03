package com.nadson.myfinance.infrastructure.adapter.web.controller;

import com.nadson.myfinance.application.port.in.CreateBudgetPort;
import com.nadson.myfinance.domain.entity.Budget;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/budgets")
public class BudgetController {

    private final CreateBudgetPort createBudgetPort;

    public BudgetController(CreateBudgetPort createBudgetPort) {
        this.createBudgetPort = createBudgetPort;
    }

    @PostMapping
    public ResponseEntity<Budget> create(
            @RequestParam UUID categoryId,
            @RequestParam int month,
            @RequestParam int year,
            @RequestParam BigDecimal limitAmount) {

        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Budget budget = createBudgetPort.execute(UUID.fromString(userId), categoryId, month, year, limitAmount);

        return ResponseEntity.status(201).body(budget);
    }
}