package com.nadson.myfinance.application.parser;

import org.apache.commons.csv.CSVRecord;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface CsvRowMapperStrategy {
    String getBankCode();
    String extractDescription(CSVRecord record);
    BigDecimal extractAmount(CSVRecord record);
    LocalDateTime extractDate(CSVRecord record);
    BigDecimal extractBalanceAfter(CSVRecord record);

    String extractReferenceId(CSVRecord record); // ADICIONE ISSO
}