package com.nadson.myfinance.infrastructure.adapter.web.controller;

import com.nadson.myfinance.application.port.in.TransferPort;
import com.nadson.myfinance.infrastructure.adapter.web.dto.request.TransferRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transfer")
public class TransferControler {
private final TransferPort transferPort;
	public TransferControler(TransferPort transferPort) {
		this.transferPort = transferPort;
	}
    @PostMapping
    public ResponseEntity<Void> execute(@RequestBody TransferRequest request) {
        transferPort.execute(request.fromId(), request.toId(), request.amount());
        return ResponseEntity.noContent().build();
    }


}
