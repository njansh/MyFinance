package com.nadson.myfinance.application.service;

import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class CategorizationEngine {

    private static final Map<String, String> KEYWORD_MAP = Map.of(
            "ATACADAO", "Mercado",
            "HORTIFRUT", "Mercado",
            "L E M", "Internet",
            "COMPANHIA ENERGETICA", "Energia",
            "CANDIDO", "Manutenção",
            "BOLT", "Transporte",
            "CHEQUE", "Salário"
    );

    private static final List<String> TRANSFER_KEYWORDS = Arrays.asList(
            "PIX", "TRANSFERENCIA", "RECURSOS", "RESERVA", "POUPANÇA", "RETIRADO", "RESGATE"
    );

    private static final List<String> IGNORED_WORDS = Arrays.asList(
            "PIX", "ENVIADO", "ENVIADA", "RECEBIDO", "RECEBIDA", "PAGAMENTO",
            "EFETUADO", "EFETUADA", "TRANSFERENCIA", "RECURSOS", "DE", "DO", "DA",
            "DOS", "DAS", "PARA", "COM", "NO", "NA", "O", "A", "OS", "AS",
            "COMPRA", "DEBITO", "CREDITO", "NUMERO"
    );

    public String process(String description) {
        if (description == null || description.isBlank()) return "Outros";

        String descUpper = description.toUpperCase();

        // 1. Tenta encontrar uma categoria fixa pelas palavras-chave de estabelecimentos
        for (Map.Entry<String, String> entry : KEYWORD_MAP.entrySet()) {
            if (descUpper.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        // 2. Se for uma movimentação genérica (PIX, Transferência, etc), retorna "Transferência"
        // em vez de tentar extrair o nome da pessoa/instituição
        for (String transferWord : TRANSFER_KEYWORDS) {
            if (descUpper.contains(transferWord)) {
                return "Transferência";
            }
        }

        // 3. Se não achar, usa a lógica de limpeza para tentar extrair algo útil (nomes de lojas específicos)
        String cleaned = descUpper.replaceAll("[^A-ZÁÉÍÓÚÀÈÌÔÛÃÕÇ ]", " ").trim();
        List<String> words = Arrays.stream(cleaned.split("\\s+"))
                .filter(word -> !IGNORED_WORDS.contains(word))
                .filter(word -> word.length() > 3)
                .toList();

        return words.isEmpty() ? "Outros" : capitalize(words.get(0));
    }

    private String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}
