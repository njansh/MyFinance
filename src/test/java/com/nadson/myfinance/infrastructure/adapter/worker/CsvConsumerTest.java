package com.nadson.myfinance.infrastructure.adapter.worker;

import com.nadson.myfinance.application.dto.CsvImportMessage;
import com.nadson.myfinance.application.service.TransactionImportService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CsvConsumerTest {

    @Mock
    private TransactionImportService transactionImportService;

    @InjectMocks
    private CsvConsumer csvConsumer;

    @Test
    @DisplayName("Cenário 1: Deve processar o arquivo e deletá-lo com sucesso")
    void shouldProcessAndDeleteFileSuccessfully() throws Exception {
        Path tempFile = Files.createTempFile("test-import", ".csv");
        UUID userId = UUID.randomUUID();
        CsvImportMessage message = mockMessage(tempFile.toAbsolutePath().toString(), userId);

        csvConsumer.consumeCsvImportMessage(message);

        verify(transactionImportService).processFile(any(File.class), eq("001"), eq(userId));
        assertFalse(Files.exists(tempFile));
    }

    @Test
    @DisplayName("Cenário 2: Deve processar mas falhar ao deletar (cobre log.warn)")
    void shouldProcessButFailToDelete() throws Exception {
        Path tempDir = Files.createTempDirectory("test-dir-import");
        Path dummyFile = Files.createTempFile(tempDir, "dummy", ".txt"); // Armazena a referência do arquivo interno

        UUID userId = UUID.randomUUID();
        CsvImportMessage message = mockMessage(tempDir.toAbsolutePath().toString(), userId);

        csvConsumer.consumeCsvImportMessage(message);

        verify(transactionImportService).processFile(any(File.class), eq("001"), eq(userId));
        assertTrue(Files.exists(tempDir));

        // Limpeza profunda sequencial para evitar DirectoryNotEmptyException
        Files.deleteIfExists(dummyFile);
        Files.deleteIfExists(tempDir);
    }

    @Test
    @DisplayName("Cenário 3: Não deve fazer nada se o arquivo não existir (cobre log.error)")
    void shouldDoNothingIfFileDoesNotExist() throws Exception {
        String fakePath = "/caminho/fake/inexistente.csv";
        UUID userId = UUID.randomUUID();
        CsvImportMessage message = mockMessage(fakePath, userId);

        csvConsumer.consumeCsvImportMessage(message);

        verify(transactionImportService, never()).processFile(any(), any(), any());
    }

    @Test
    @DisplayName("Cenário 4: Deve capturar e logar Exception (cobre bloco catch)")
    void shouldCatchAndLogException() throws Exception {
        Path tempFile = Files.createTempFile("test-error", ".csv");
        UUID userId = UUID.randomUUID();
        CsvImportMessage message = mockMessage(tempFile.toAbsolutePath().toString(), userId);

        doThrow(new RuntimeException("Erro simulado"))
                .when(transactionImportService)
                .processFile(any(File.class), eq("001"), eq(userId));

        csvConsumer.consumeCsvImportMessage(message);

        verify(transactionImportService).processFile(any(File.class), eq("001"), eq(userId));

        Files.deleteIfExists(tempFile);
    }

    private CsvImportMessage mockMessage(String path, UUID userId) {
        CsvImportMessage message = mock(CsvImportMessage.class);
        lenient().when(message.filePath()).thenReturn(path);
        lenient().when(message.bankCode()).thenReturn("001");
        lenient().when(message.userId()).thenReturn(userId);
        lenient().when(message.originalFileName()).thenReturn("teste.csv");
        return message;
    }
}