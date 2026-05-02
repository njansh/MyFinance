package com.nadson.myfinance.application.parser;

import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class InterRowMapper implements CsvRowMapperStrategy {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public String getBankCode() { return "INTER"; }

    @Override
    public String extractDescription(CSVRecord record) {
        // Índice 1 = Histórico, Índice 2 = Descrição
        return (record.get(1) + " " + record.get(2)).trim();
    }

    @Override
    public BigDecimal extractAmount(CSVRecord record) {
        // Índice 3 = Valor
        return new BigDecimal(record.get(3).replace(".", "").replace(",", "."));
    }

    @Override
    public LocalDateTime extractDate(CSVRecord record) {
        // Índice 0 = Data Lançamento
        return LocalDate.parse(record.get(0).trim(), FORMATTER).atStartOfDay();
    }

    @Override
    public BigDecimal extractBalanceAfter(CSVRecord record) {
        // Índice 4 = Saldo
        try {
            String balanceStr = record.get(4);
            if (balanceStr!= null &&!balanceStr.isBlank()) {
                return new BigDecimal(balanceStr.replace(".", "").replace(",", "."));
            }
        } catch (Exception e) {
            return null; // Caso a coluna venha vazia
        }
        return null;
    }
}