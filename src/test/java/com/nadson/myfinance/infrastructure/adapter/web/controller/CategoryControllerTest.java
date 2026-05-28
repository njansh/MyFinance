package com.nadson.myfinance.infrastructure.adapter.web.controller;

import com.nadson.myfinance.application.port.in.*;
import com.nadson.myfinance.application.usecase.DeleteCategoryUseCase;
import com.nadson.myfinance.domain.entity.Category;
import com.nadson.myfinance.domain.enums.TransactionType;
import com.nadson.myfinance.infrastructure.adapter.web.dto.request.CategoryRequest;
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
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CategoryController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com.nadson.myfinance.infrastructure.security.*"))
@WithMockUserId
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private CreateCategoryPort createCategoryPort;
    @MockBean private GetCategoriesPort getCategoriesPort;
    @MockBean private UpdateCategoryPort updateCategoryPort;
    @MockBean private DeleteCategoryUseCase deleteCategoryUseCase;

    private final String MOCK_USER_ID = "d6e3e5b0-1234-4321-8765-abcdef123456";

    @Test
    @DisplayName("Deve criar categoria com sucesso")
    void shouldCreateCategory() throws Exception {
        UUID userId = UUID.fromString(MOCK_USER_ID);
        Category category = new Category(UUID.randomUUID(), userId, "Lazer", "#000000", "icon", TransactionType.EXPENSE);

        when(createCategoryPort.execute(eq(userId), eq("Lazer"), eq("#000000"), eq("icon"), eq(TransactionType.EXPENSE)))
                .thenReturn(category);

        String json = """
            {"name": "Lazer", "colorHex": "#000000", "icon": "icon", "type": "EXPENSE"}
            """;

        mockMvc.perform(post("/categories")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Deve listar todas as categorias")
    void shouldGetAllCategories() throws Exception {
        when(getCategoriesPort.execute(any())).thenReturn(List.of());

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve deletar categoria")
    void shouldDeleteCategory() throws Exception {
        UUID id = UUID.randomUUID();
        mockMvc.perform(delete("/categories/" + id).with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve atualizar categoria")
    void shouldUpdateCategory() throws Exception {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.fromString(MOCK_USER_ID);
        Category category = new Category(id, userId, "Trabalho", "#FFFFFF", "icon", TransactionType.INCOME);

        when(updateCategoryPort.execute(eq(userId), eq(id), eq("Trabalho"), eq("#FFFFFF"), eq("icon"), eq(TransactionType.INCOME)))
                .thenReturn(category);

        String json = """
            {"name": "Trabalho", "colorHex": "#FFFFFF", "icon": "icon", "type": "INCOME"}
            """;

        mockMvc.perform(put("/categories/" + id)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }
}