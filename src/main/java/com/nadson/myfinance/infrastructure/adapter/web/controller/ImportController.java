package com.nadson.myfinance.infrastructure.adapter.web.controller;

import com.nadson.myfinance.application.service.TransactionImportService;
import org.springframework.http.MediaType; // Importante!
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @PostMapping(value = "/{bankCode}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> importStatement(
            @PathVariable String bankCode,
            @RequestPart("file") MultipartFile file) {
        try {
            String authenticatedUserId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            importService.importCsv(file, bankCode.toUpperCase(), UUID.fromString(authenticatedUserId));

            return ResponseEntity.ok("Importação para " + bankCode.toUpperCase() + " concluída com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Import error: " + e.getMessage());
        }
    }


}