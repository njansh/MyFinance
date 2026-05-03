package com.nadson.myfinance.application.service;

import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategorizationEngine {

    private static final List<String> IGNORED_WORDS = Arrays.asList(
            "PIX", "ENVIADO", "ENVIADA", "RECEBIDO", "RECEBIDA", "PAGAMENTO",
            "EFETUADO", "EFETUADA", "TRANSFERENCIA", "RECURSOS",
            "DE", "DO", "DA", "DOS", "DAS", "PARA", "COM", "NO", "NA", "O", "A", "OS", "AS",
            "COMPRA", "DEBITO", "CREDITO"
    );

    public String process(String description) {
        if (description == null || description.isBlank()) {
            return "Outros";
        }

        String cleaned = description.toUpperCase()
                .replaceAll("[^A-ZÁÉÍÓÚÀÈÌÒÙÂÊÎÔÛÃÕÇ ]", " ")
                .trim();

        List<String> words = Arrays.stream(cleaned.split("\\s+"))
                .filter(word -> !IGNORED_WORDS.contains(word))
                .filter(word -> word.length() > 2)
                .collect(Collectors.toList());

        if (words.isEmpty()) {
            return "Outros";
        }

        return capitalize(words.get(0));
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}
