package com.nadson.myfinance.infrastructure.adapter.web.controller;

import com.nadson.myfinance.application.service.TransactionImportService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ImportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionImportService importService;

    private final String USER_ID = "550e8400-e29b-41d4-a716-446655440000";
    private final UUID USER_UUID = UUID.fromString(USER_ID);

    @Test
    @DisplayName("Should return 200 when CSV is imported successfully")
    void shouldImportCsvSuccessfully() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "extrato.csv",
                "text/csv",
                "data,amount,description\n2026-05-01,100.00,Teste".getBytes()
        );

        doNothing().when(importService).importCsv(any(), eq("NUBANK"), eq(USER_UUID));

        var auth = new UsernamePasswordAuthenticationToken(USER_ID, null, Collections.emptyList());

        mockMvc.perform(multipart("/api/import/{bankCode}", "NUBANK")
                        .file(file)
                        .with(csrf())
                        .with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(content().string("Importação para NUBANK concluída com sucesso!"));
    }

    @Test
    @DisplayName("Should return 403 when importing without authentication")
    void shouldReturn403WhenUnauthenticated() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", "content".getBytes());

        mockMvc.perform(multipart("/api/import/NUBANK")
                        .file(file)
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }
}