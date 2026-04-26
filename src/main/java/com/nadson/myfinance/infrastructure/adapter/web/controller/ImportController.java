package com.nadson.myfinance.infrastructure.adapter.web.controller;

import com.nadson.myfinance.application.service.TransactionImportService;
import org.springframework.http.MediaType; // Importante!
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/import")
public class ImportController {

    private final TransactionImportService importService;

    public ImportController(TransactionImportService importService) {
        this.importService = importService;
    }

    @PostMapping(value = "/mercado-pago", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> importMercadoPago(
            @RequestPart("file") MultipartFile file, // Mudamos de @RequestParam para @RequestPart
            @RequestHeader("X-User-Id") UUID userId) {
        try {
            importService.importCsv(file, "MP", userId);
            return ResponseEntity.ok("Mercado Pago import completed successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Import error: " + e.getMessage());
        }
    }

    @PostMapping(value = "/inter", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> importInter(
            @RequestPart("file") MultipartFile file, // Mudamos de @RequestParam para @RequestPart
            @RequestHeader("X-User-Id") UUID userId) {
        try {
            importService.importCsv(file, "INTER", userId);
            return ResponseEntity.ok("Inter import completed successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Import error: " + e.getMessage());
        }
    }
}