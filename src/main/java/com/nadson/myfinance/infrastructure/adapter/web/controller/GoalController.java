package com.nadson.myfinance.infrastructure.adapter.web.controller;

import com.nadson.myfinance.application.port.in.CreateGoalPort;
import com.nadson.myfinance.domain.entity.Goal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/goals")
public class GoalController {

    private final CreateGoalPort createGoalPort;

    public GoalController(CreateGoalPort createGoalPort) {
        this.createGoalPort = createGoalPort;
    }

    @PostMapping
    public ResponseEntity<Goal> create(
            @RequestParam String description,
            @RequestParam BigDecimal targetAmount) {

        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Goal goal = createGoalPort.execute(UUID.fromString(userId), description, targetAmount);

        return ResponseEntity.status(201).body(goal);
    }
}