package com.nadson.myfinance.infrastructure.adapter.web.controller;

import com.nadson.myfinance.application.port.in.CreateGoalPort;
import com.nadson.myfinance.application.port.in.DeleteGoalPort;
import com.nadson.myfinance.application.port.in.ListGoalsPort;
import com.nadson.myfinance.application.port.in.UpdateGoalPort;
import com.nadson.myfinance.domain.entity.Goal;
import com.nadson.myfinance.infrastructure.adapter.web.dto.request.CreateGoalRequest;
import com.nadson.myfinance.infrastructure.adapter.web.dto.request.UpdateGoalRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/goals")
public class GoalController {

    private final CreateGoalPort createGoalPort;
    private final ListGoalsPort listGoalsPort;
    private final UpdateGoalPort updateGoalPort;
    private final DeleteGoalPort deleteGoalPort;

    public GoalController(
            CreateGoalPort createGoalPort,
            ListGoalsPort listGoalsPort,
            UpdateGoalPort updateGoalPort,
            DeleteGoalPort deleteGoalPort) {
        this.createGoalPort = createGoalPort;
        this.listGoalsPort = listGoalsPort;
        this.updateGoalPort = updateGoalPort;
        this.deleteGoalPort = deleteGoalPort;
    }

    @PostMapping
    public ResponseEntity<Goal> create(@RequestBody CreateGoalRequest request) {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Goal goal = createGoalPort.execute(
                UUID.fromString(userId),
                request.description(),
                request.targetAmount(),
                request.accountIds()
        );

        return ResponseEntity.status(201).body(goal);
    }

    @GetMapping
    public ResponseEntity<List<Goal>> listByUser() {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Agora usamos o UseCase ao invés do Repositório direto
        List<Goal> goals = listGoalsPort.execute(UUID.fromString(userId));
        return ResponseEntity.ok(goals);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Goal> update(@PathVariable UUID id, @RequestBody UpdateGoalRequest request) {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Goal goal = updateGoalPort.execute(
                UUID.fromString(userId),
                id,
                request.description(),
                request.targetAmount(),
                request.accountIds()
        );

        return ResponseEntity.ok(goal);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        deleteGoalPort.execute(id, UUID.fromString(userId));

        return ResponseEntity.noContent().build();
    }
}