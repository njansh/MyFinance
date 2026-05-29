package com.nadson.myfinance.infrastructure.adapter.out;

import com.nadson.myfinance.application.dto.CsvImportMessage;
import com.nadson.myfinance.infrastructure.config.RabbitMQConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CsvProducerTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private CsvProducer csvProducer;

    @Test
    @DisplayName("Deve enviar a mensagem de importação de CSV para a fila do RabbitMQ")
    void shouldSendCsvImportMessage() {
        // Arrange - Cria um mock da mensagem para evitar dependência do construtor real
        CsvImportMessage message = mock(CsvImportMessage.class);
        when(message.userId()).thenReturn(UUID.randomUUID());

        // Act - Invoca o produtor
        csvProducer.sendCsvImportMessage(message);

        // Assert - Verifica se o produtor despachou para a fila configurada corretamente
        verify(rabbitTemplate).convertAndSend(RabbitMQConfig.CSV_IMPORT_QUEUE, message);
    }
}