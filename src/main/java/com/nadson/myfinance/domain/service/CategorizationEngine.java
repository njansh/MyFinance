package com.nadson.myfinance.domain.service;

import java.util.HashMap;
import java.util.Map;

public class CategorizationEngine {

    private static final Map<String, String> RULES = new HashMap<>();

    static {
        RULES.put("ifood", "Alimentação");
        RULES.put("restaurante", "Alimentação");
        RULES.put("padaria", "Alimentação");
        RULES.put("mercado", "Alimentação");
        RULES.put("supermercado", "Alimentação");
        RULES.put("uber", "Lazer e Viagens");
        RULES.put("99pop", "Lazer e Viagens");
        RULES.put("netflix", "Lazer e Viagens");
        RULES.put("spotify", "Lazer e Viagens");
        RULES.put("cinema", "Lazer e Viagens");
        RULES.put("hotel", "Lazer e Viagens");
        RULES.put("farmacia", "Saúde");
        RULES.put("drogaria", "Saúde");
        RULES.put("hospital", "Saúde");
        RULES.put("medico", "Saúde");
        RULES.put("aluguel", "Moradia");
        RULES.put("condominio", "Moradia");
        RULES.put("luz", "Contas Fixas");
        RULES.put("energia", "Contas Fixas");
        RULES.put("agua", "Contas Fixas");
        RULES.put("internet", "Contas Fixas");
        RULES.put("celular", "Contas Fixas");
        RULES.put("telefone", "Contas Fixas");
        RULES.put("salario", "Salário");
        RULES.put("pix", "Transferência");
        RULES.put("freelance", "Freelance");
        RULES.put("amazon", "Compras");
        RULES.put("shopee", "Compras");
        RULES.put("mercado livre", "Compras");
        RULES.put("magalu", "Compras");
        RULES.put("posto", "Transporte");
        RULES.put("combustivel", "Transporte");
        RULES.put("gasolina", "Transporte");
        RULES.put("academia", "Saúde");
        RULES.put("dentista", "Saúde");
        RULES.put("seguro", "Seguros");
        RULES.put("iptu", "Impostos");
        RULES.put("ipva", "Impostos");
    }

    public static String sanitizeDescription(String rawDescription) {
        if (rawDescription == null) return "";
        return rawDescription.toLowerCase()
                .replaceAll("[^a-zà-ú\\s]", " ") 
                .replaceAll("\\s+", " ")        
                .trim();
    }


    public static String suggestCategoryName(String rawDescription) {
        String sanitized = sanitizeDescription(rawDescription);
        for (Map.Entry<String, String> rule : RULES.entrySet()) {
            if (sanitized.contains(rule.getKey())) {
                return rule.getValue();
            }
        }
        return null;
    }
}