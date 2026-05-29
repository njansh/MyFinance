package com.nadson.myfinance.infrastructure.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RabbitMQConfigTest {

    @Test
    @DisplayName("Deve cobrir a criação das filas e conversores do RabbitMQ")
    void shouldCreateRabbitMQBeans() {
        RabbitMQConfig config = new RabbitMQConfig();

        Queue queue = config.csvImportQueue();
        Jackson2JsonMessageConverter converter = (Jackson2JsonMessageConverter) config.jsonMessageConverter();

        assertNotNull(queue);
        assertEquals(RabbitMQConfig.CSV_IMPORT_QUEUE, queue.getName());
        assertNotNull(converter);
    }
}