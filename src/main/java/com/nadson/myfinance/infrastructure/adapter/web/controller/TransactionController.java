package com.nadson.myfinance.infrastructure.adapter.web.controller;

import com.nadson.myfinance.application.port.in.CreateTransactionPort;
import com.nadson.myfinance.application.port.in.GetTransactionPort;
import com.nadson.myfinance.domain.entity.Transaction;
import com.nadson.myfinance.infrastructure.adapter.web.dto.request.TransactionRequest;
import com.nadson.myfinance.infrastructure.adapter.web.dto.response.TransactionResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private final CreateTransactionPort createTransactionPort;
    private final GetTransactionPort getTransactionPort;

    public TransactionController(CreateTransactionPort createTransactionPort, GetTransactionPort getTransactionPort) {
        this.createTransactionPort = createTransactionPort;
        this.getTransactionPort = getTransactionPort;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> create(@Valid @RequestBody TransactionRequest request) {
        Transaction transaction = new Transaction(
                UUID.randomUUID(),
                request.description(),
                request.amount(),
                java.time.LocalDateTime.now(),
                request.type(),
                request.accountId(),
                request.categoryId(),
                request.isTransfer()
        );
        Transaction createdTransaction = createTransactionPort.execute(transaction);
        return ResponseEntity.status(201).body(TransactionResponse.fromDomain(createdTransaction));

}
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getById(@PathVariable UUID id) {
        Transaction transaction = getTransactionPort.execute(id);
        return ResponseEntity.ok(TransactionResponse.fromDomain(transaction));
    }}
