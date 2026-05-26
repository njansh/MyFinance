package com.nadson.myfinance.application.dto;

import java.io.Serializable;
import java.util.UUID;

public record CsvImportMessage(
        UUID userId,
        String originalFileName,
        String filePath,
        String bankCode
) implements Serializable {}