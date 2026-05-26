package com.nadson.myfinance.infrastructure.adapter.web.controller;

import com.nadson.myfinance.application.dto.CsvImportMessage;
import com.nadson.myfinance.infrastructure.adapter.out.CsvProducer;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@RestController
@RequestMapping("/api/import")
public class ImportController {

    private final CsvProducer csvProducer;

    public ImportController(CsvProducer csvProducer) {
        this.csvProducer = csvProducer;
    }

    @PostMapping(value = "/{bankCode}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> importStatement(
            @PathVariable String bankCode,
            @RequestPart("file") MultipartFile file) {
        try {
            String authenticatedUserId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UUID userId = UUID.fromString(authenticatedUserId);

            Path tempDir = Files.createTempDirectory("myfinance-import-");
            Path tempFile = tempDir.resolve(file.getOriginalFilename());
            file.transferTo(tempFile.toFile());

            CsvImportMessage message = new CsvImportMessage(
                    userId,
                    file.getOriginalFilename(),
                    tempFile.toAbsolutePath().toString(),
                    bankCode
            );

            csvProducer.sendCsvImportMessage(message);

            return ResponseEntity.accepted()
                    .body("Importação para " + bankCode.toUpperCase() + " iniciada. Processando em segundo plano.");

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Erro ao preparar arquivo: " + e.getMessage());
        }
    }
}