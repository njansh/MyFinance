package com.nadson.myfinance.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class CategorizationEngineTest {

    private final CategorizationEngine engine = new CategorizationEngine();

    @Test
    @DisplayName("Deve retornar categoria mapeada via KEYWORD_MAP")
    void shouldReturnCategoryFromKeywordMap() {
        assertThat(engine.process("COMPRA NO ATACADAO")).isEqualTo("Mercado");
        assertThat(engine.process("PAGAMENTO DE BOLT")).isEqualTo("Transporte");
        assertThat(engine.process("MANUTENCAO CANDIDO")).isEqualTo("Manutenção");
    }

    @Test
    @DisplayName("Deve retornar 'Transferência' para palavras-chave de transferência")
    void shouldReturnTransferenciaForKeywords() {
        assertThat(engine.process("PIX ENVIADO")).isEqualTo("Transferência");
        assertThat(engine.process("RESERVA DE DINHEIRO")).isEqualTo("Transferência");
        assertThat(engine.process("RESGATE DE INVESTIMENTO")).isEqualTo("Transferência");
    }

    @Test
    @DisplayName("Deve retornar 'Outros' para descrições vazias ou nulas")
    void shouldReturnOutrosForInvalidDescription() {
        assertThat(engine.process(null)).isEqualTo("Outros");
        assertThat(engine.process("   ")).isEqualTo("Outros");
    }

    @Test
    @DisplayName("Deve limpar descrição e extrair nome de loja válida (Capitalized)")
    void shouldExtractAndCapitalizeStoreName() {
        // "PADARIA DO ZE" -> ignora "DO", filtra "PADARIA" (comp > 3 letras)
        assertThat(engine.process("PADARIA DO ZE")).isEqualTo("Padaria");
    }

    @Test
    @DisplayName("Deve retornar 'Outros' quando não houver palavras com mais de 3 letras após limpeza")
    void shouldReturnOutrosWhenNoValidWordsFound() {
        // "DO" e "PIX" são ignoradas ou pequenas demais
        assertThat(engine.process("DO PIX")).isEqualTo("Outros");
    }

    @Test
    @DisplayName("Deve tratar caracteres especiais e limpar ruídos")
    void shouldHandleSpecialCharacters() {
        // Remove símbolos e mantém letras acentuadas (se presentes)
        assertThat(engine.process("COMPRA #123 CAFÉ ")).isEqualTo("Café");
    }
}