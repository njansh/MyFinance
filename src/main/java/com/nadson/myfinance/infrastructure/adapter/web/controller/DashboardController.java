package com.nadson.myfinance.infrastructure.adapter.web.controller;

import com.nadson.myfinance.application.port.in.GetFinancialDashboardKpisPort;
import com.nadson.myfinance.infrastructure.adapter.web.dto.response.KpiDashboardResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final GetFinancialDashboardKpisPort kpisPort;

    public DashboardController(GetFinancialDashboardKpisPort kpisPort) {
        this.kpisPort = kpisPort;
    }

    @GetMapping("/kpis")
    public ResponseEntity<KpiDashboardResponse> getKpis(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {

        String authenticatedUserId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        KpiDashboardResponse response = kpisPort.execute(UUID.fromString(authenticatedUserId), month, year);
        return ResponseEntity.ok(response);
    }
}