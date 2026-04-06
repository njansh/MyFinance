package com.nadson.myfinance.infrastructure.adapter.web.controller;

import com.nadson.myfinance.application.port.in.CreateUserPort;
import com.nadson.myfinance.application.port.in.GetUserPort;
import com.nadson.myfinance.domain.entity.User;
import com.nadson.myfinance.infrastructure.adapter.web.dto.request.UserRequest;
import com.nadson.myfinance.infrastructure.adapter.web.dto.response.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {
    private final CreateUserPort createUserPort;
    private final GetUserPort getUserPort;

    public UserController(CreateUserPort createUserPort, GetUserPort getUserPort) {
        this.createUserPort = createUserPort;
        this.getUserPort = getUserPort;
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
}
