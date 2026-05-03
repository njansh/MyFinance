package com.nadson.myfinance.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CategorizationEngineTest {

    private final CategorizationEngine engine = new CategorizationEngine();

    @ParameterizedTest
    @DisplayName("Deve limpar e categorizar descrições vindas de extratos bancários")
    @CsvSource({
            // Casos originais
            "'PAGAMENTO * MERCADO EXTRA', 'Mercado'",
            "'TRANSFERENCIA RECEBIDA - NADSON', 'Nadson'",
            "'UBER * TRIP HELPER', 'Uber'",
            "'PIX ENVIADA PARA JOAO', 'Joao'",

            // Casos de ruído bancário (Prefixo que deve ser ignorado)
            "'PIX ENVIADO - SUPERMERCADO ABC', 'Supermercado'",
            "'COMPRA NO DEBITO POSTO SHELL', 'Posto'",
            "'PAGAMENTO EFETUADO FARMACIA PAG MENOS', 'Farmacia'",
            "'PIX RECEBIDO DE MARIA SILVA', 'Maria'",

            // Casos com números e caracteres especiais
            "'REFEICAO 123456789', 'Refeicao'",
            "'RESTAURANTE @ SABOR!', 'Restaurante'",
            "'LANCHONETE/PADARIA', 'Lanchonete'",

            // Normalização de texto (Capitalização)
            "'mErCaDo eXtRa', 'Mercado'",
            "'  loja de moveis  ', 'Loja'",

            // Casos de borda
            "'', 'Outros'",
            " , 'Outros'",
            "'A', 'Outros'", // Palavras muito curtas que não ajudam na categoria
            "'DE', 'Outros'"
    })
    void shouldCleanAndCategorizeDescriptionCorrectly(String input, String expected) {
        String result = engine.process(input);

        assertEquals(expected, result,
                String.format("Falha ao processar: '%s'", input));
    }

    @ParameterizedTest
    @DisplayName("Deve ignorar conectores e artigos no início da descrição")
    @CsvSource({
            "'O BOTICARIO', 'Boticario'",
            "'A GOL TRANSPORTES', 'Gol'",
            "'OS IRMAOS PAIVA', 'Irmaos'",
            "'DAS ARARAS LANCHES', 'Araras'"
    })
    void shouldIgnoreShortConnectorsAtStart(String input, String expected) {
        String result = engine.process(input);
        assertEquals(expected, result);
    }
}