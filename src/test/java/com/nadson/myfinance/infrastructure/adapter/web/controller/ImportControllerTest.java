package com.nadson.myfinance.infrastructure.adapter.web.controller;

import com.nadson.myfinance.infrastructure.adapter.out.CsvProducer;
import com.nadson.myfinance.infrastructure.security.WithMockUserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ImportController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com.nadson.myfinance.infrastructure.security.*"))
@WithMockUserId
@AutoConfigureMockMvc(addFilters = false)
class ImportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CsvProducer csvProducer;

    @Test
    @DisplayName("Deve iniciar importação de CSV com sucesso")
    void shouldImportStatementSuccessfully() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "extrato.csv", MediaType.TEXT_PLAIN_VALUE, "data,amount,desc".getBytes()
        );

        doNothing().when(csvProducer).sendCsvImportMessage(any());

        mockMvc.perform(multipart("/api/import/nubank")
                        .file(file)
                        .with(csrf()))
                .andExpect(status().isAccepted());
    }

    @Test
    @DisplayName("Deve retornar erro ao falhar na manipulação do arquivo")
    void shouldReturnErrorWhenFileTransferFails() throws Exception {
        // Mockando um arquivo vazio que força erro na transferência
        MockMultipartFile file = new MockMultipartFile("file", "", null, (byte[]) null);

        mockMvc.perform(multipart("/api/import/nubank")
                        .file(file)
                        .with(csrf()))
                .andExpect(status().isInternalServerError());
    }
}