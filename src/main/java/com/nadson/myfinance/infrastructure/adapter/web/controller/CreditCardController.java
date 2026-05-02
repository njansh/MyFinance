package com.nadson.myfinance.infrastructure.adapter.web.controller;

import com.nadson.myfinance.application.port.in.ProcessCreditCardTransactionPort;
import com.nadson.myfinance.infrastructure.adapter.web.dto.request.CreditCardRequest;
import com.nadson.myfinance.infrastructure.adapter.web.dto.response.CreditCardTransactionRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/credit-cards")
public class CreditCardController {
    private final CreateCreditCardPort createCreditCardPort;
    private final ProcessCreditCardTransactionPort processTransactionPort;

    public CreditCardController(CreateCreditCardPort createCreditCardPort, ProcessCreditCardTransactionPort processTransactionPort) {
        this.createCreditCardPort = createCreditCardPort;
        this.processTransactionPort = processTransactionPort;
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody CreditCardRequest request) {
        createCreditCardPort.execute(request.name(), request.creditLimit(), request.closingDay(), request.dueDay(), request.accountId());
        return ResponseEntity.status(201).build();
    }

    @PostMapping("/{id}/transactions")
    public ResponseEntity<Void> addTransaction(@PathVariable UUID id, @RequestBody CreditCardTransactionRequest request) {
        processTransactionPort.execute(id, request.amount(), request.date(), request.installments());
        return ResponseEntity.ok().build();
    }
}
