package com.nadson.myfinance.infrastructure.adapter.worker;

import com.nadson.myfinance.application.dto.CsvImportMessage;
import com.nadson.myfinance.application.service.TransactionImportService;
import com.nadson.myfinance.infrastructure.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import java.io.File;

@Component
public class CsvConsumer {
    private static final Logger log = LoggerFactory.getLogger(CsvConsumer.class);
    private final TransactionImportService transactionImportService;

    public CsvConsumer(TransactionImportService transactionImportService) {
        this.transactionImportService = transactionImportService;
    }

    @RabbitListener(queues = RabbitMQConfig.CSV_IMPORT_QUEUE)
    public void consumeCsvImportMessage(CsvImportMessage message) {
        log.info("Recebendo arquivo para processamento: {}", message.filePath());
        try {
            File file = new File(message.filePath());
            if (file.exists()) {
                transactionImportService.processFile(file, message.bankCode(), message.userId());
                log.info("Processamento finalizado com sucesso: {}", message.originalFileName());
                if (!file.delete()) log.warn("Não foi possível apagar o arquivo temporário.");
            } else {
                log.error("Arquivo não encontrado no caminho: {}", message.filePath());
            }
        } catch (Exception e) {
            log.error("Falha crítica no processamento da mensagem para o usuário: {}", message.userId(), e);
        }
    }
}