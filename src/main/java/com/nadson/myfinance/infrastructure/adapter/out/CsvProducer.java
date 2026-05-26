package com.nadson.myfinance.infrastructure.adapter.out;

import com.nadson.myfinance.application.dto.CsvImportMessage;
import com.nadson.myfinance.infrastructure.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class CsvProducer {

    private static final Logger log = LoggerFactory.getLogger(CsvProducer.class);
    private final RabbitTemplate rabbitTemplate;

    public CsvProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendCsvImportMessage(CsvImportMessage message) {
        log.info("Sending CSV import message for user: {}", message.userId());
        rabbitTemplate.convertAndSend(RabbitMQConfig.CSV_IMPORT_QUEUE, message);
    }
}