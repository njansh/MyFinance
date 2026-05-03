package com.nadson.myfinance.infrastructure.adapter.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nadson.myfinance.application.port.in.CreateCategoryPort;
import com.nadson.myfinance.application.port.in.GetCategoriesPort;
import com.nadson.myfinance.domain.entity.Category;
import com.nadson.myfinance.domain.enums.TransactionType;
import com.nadson.myfinance.infrastructure.adapter.web.dto.request.CategoryRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CreateCategoryPort createCategoryPort;

    @MockBean
    private GetCategoriesPort getCategoriesPort;

    private final String USER_ID = "550e8400-e29b-41d4-a716-446655440000";
    private final UUID USER_UUID = UUID.fromString(USER_ID);

    @Test
    @DisplayName("Should create category and return 201")
    void shouldCreateCategory() throws Exception {
        CategoryRequest request = new CategoryRequest("Alimentação", "#FF5733", TransactionType.EXPENSE);
        Category mockCategory = new Category(UUID.randomUUID(), USER_UUID, "Alimentação", "#FF5733", TransactionType.EXPENSE);

        when(createCategoryPort.execute(eq(USER_UUID), any(), any(), any()))
                .thenReturn(mockCategory);

        var auth = new UsernamePasswordAuthenticationToken(USER_ID, null, Collections.emptyList());

        mockMvc.perform(post("/categories")
                        .with(csrf())
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Alimentação"))
                .andExpect(jsonPath("$.colorHex").value("#FF5733"));
    }

    @Test
    @DisplayName("Should return list of categories and 200")
    void shouldListCategories() throws Exception {
        Category category = new Category(UUID.randomUUID(), USER_UUID, "Salário", "#00FF00", TransactionType.INCOME);

        when(getCategoriesPort.execute(USER_UUID)).thenReturn(List.of(category));

        var auth = new UsernamePasswordAuthenticationToken(USER_ID, null, Collections.emptyList());

        mockMvc.perform(get("/categories")
                        .with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Salário"))
                .andExpect(jsonPath("$[0].type").value("INCOME"));
    }

    @Test
    @DisplayName("Should return 403 when accessing without authentication")
    void shouldReturn403WhenUnauthenticated() throws Exception {
        mockMvc.perform(get("/categories"))
                .andExpect(status().isForbidden());
    }
}
