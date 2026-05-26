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
        StringBuilder sb = new StringBuilder();
        int lastIndex = record.size() - 1; // Saldo
        int valueIndex = lastIndex - 1;    // Valor

        // Percorre as colunas do meio (Histórico e Descrição)
        for (int i = 1; i < valueIndex; i++) {
            String val = record.get(i);
            // IGNORA colunas vazias geradas por ;; no CSV
            if (val != null && !val.isBlank()) {
                sb.append(val.trim()).append(" ");
            }
        }

        String description = sb.toString().trim();
        return description.isEmpty() ? "Transação sem descrição" : description;
    }

    @Override
    public BigDecimal extractAmount(CSVRecord record) {
        String value = record.get(record.size() - 2);
        if (value == null || value.isBlank()) return BigDecimal.ZERO; // Segurança extra
        return new BigDecimal(value.replace(".", "").replace(",", "."));
    }

    @Override
    public LocalDateTime extractDate(CSVRecord record) {
        return LocalDate.parse(record.get(0).trim(), FORMATTER).atStartOfDay();
    }

    @Override
    public BigDecimal extractBalanceAfter(CSVRecord record) {
        try {
            String balanceStr = record.get(record.size() - 1);
            if (balanceStr != null && !balanceStr.isBlank()) {
                return new BigDecimal(balanceStr.replace(".", "").replace(",", "."));
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    @Override
    public String extractReferenceId(CSVRecord record) {
        // Cria um Pseudo-ID forte para o Inter (Data_Descrição_Valor)
        // Isso garante que a deduplicação funcione da mesma forma que no Mercado Pago
        String date = record.get(0).trim();
        String desc = extractDescription(record);
        String amount = record.get(record.size() - 2).trim();

        return (date + "|" + desc + "|" + amount).replaceAll("\\s+", "_");
    }
}