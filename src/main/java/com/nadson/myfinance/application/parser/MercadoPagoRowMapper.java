package com.nadson.myfinance.application.parser;

import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.text.NumberFormat;

@Component
public class MercadoPagoRowMapper implements CsvRowMapperStrategy {

    // O CSV do MP utiliza traços para separar as datas
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Override
    public String getBankCode() { return "MP"; }

    @Override
    public String extractDescription(CSVRecord record) {
        // Índice 1 = TRANSACTION_TYPE, Índice 2 = REFERENCE_ID
        // A regra de negócio para evitar falsas duplicatas exige o Ref ID no MP
        return record.get(1).trim() + " (Ref: " + record.get(2).trim() + ")";
    }

    @Override
    public BigDecimal extractAmount(CSVRecord record) {
        // Índice 3 = TRANSACTION_NET_AMOUNT
        return parseCurrency(record.get(3));
    }

    @Override
    public LocalDateTime extractDate(CSVRecord record) {
        // Índice 0 = RELEASE_DATE
        // Faz o parse apenas da data (LocalDate) e converte para LocalDateTime
        return LocalDate.parse(record.get(0).trim(), FORMATTER).atStartOfDay();
    }

    @Override
    public BigDecimal extractBalanceAfter(CSVRecord record) {
        // Índice 4 = PARTIAL_BALANCE
        try {
            String balance = record.get(4);
            return (balance!= null &&!balance.isBlank())? parseCurrency(balance) : null;
        } catch (Exception e) {
            return null; // Caso não possua saldo parcial mapeável na linha
        }
    }

    private BigDecimal parseCurrency(String value) {
        try {
            String cleanValue = value.trim();
            NumberFormat format = NumberFormat.getInstance(new Locale("pt", "BR"));
            return new BigDecimal(format.parse(cleanValue).toString());
        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar valor monetário: " + value, e);
        }
    }
}