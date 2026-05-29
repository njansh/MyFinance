package com.nadson.myfinance.domain.entity;

import com.nadson.myfinance.domain.enums.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CategoryTest {

    @Test
    @DisplayName("Deveria criar categoria com sucesso e cobrir getters")
    void shouldCreateCategorySuccessfully() {
        UUID categoryId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Category category = new Category(categoryId, userId, "Alimentação", "#FFFFFF", "Fastfood", TransactionType.EXPENSE);

        // Testa getters (o que eleva a cobertura para perto de 100%)
        assertThat(category.getCategoryId()).isEqualTo(categoryId);
        assertThat(category.getUserId()).isEqualTo(userId);
        assertThat(category.getName()).isEqualTo("Alimentação");
        assertThat(category.getColorHex()).isEqualTo("#FFFFFF");
        assertThat(category.getIcon()).isEqualTo("Fastfood");
        assertThat(category.getType()).isEqualTo(TransactionType.EXPENSE);
    }

    @Test
    @DisplayName("Deveria disparar erro se nome for nulo ou vazio")
    void shouldFailWhenNameIsInvalid() {
        assertThrows(IllegalArgumentException.class, () ->
                new Category(null, null, null, "#FFFFFF", "Icon", TransactionType.EXPENSE));

        assertThrows(IllegalArgumentException.class, () ->
                new Category(null, null, "  ", "#FFFFFF", "Icon", TransactionType.EXPENSE));
    }

    @Test
    @DisplayName("Deveria disparar erro se formato da cor for inválido")
    void shouldFailWhenColorHexIsInvalid() {
        // Formatos inválidos para o regex #[0-9a-fA-F]{6}
        assertThrows(IllegalArgumentException.class, () ->
                new Category(null, null, "Nome", "FFFFFF", "Icon", TransactionType.EXPENSE)); // Falta #

        assertThrows(IllegalArgumentException.class, () ->
                new Category(null, null, "Nome", "#GGGGGG", "Icon", TransactionType.EXPENSE)); // Hex inválido
    }

    @Test
    @DisplayName("Deveria definir ícone padrão se for nulo ou vazio")
    void shouldSetDefaultIcon() {
        Category category = new Category(null, null, "Lazer", "#000000", null, TransactionType.INCOME);
        assertThat(category.getIcon()).isEqualTo("Circle");
    }
}