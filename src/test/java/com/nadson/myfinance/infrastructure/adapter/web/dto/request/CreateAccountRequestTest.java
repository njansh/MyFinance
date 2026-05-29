package com.nadson.myfinance.infrastructure.adapter.web.dto.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CreateAccountRequestTest {

    @Test
    @DisplayName("Should cover the parameterized constructor and getters")
    void testCreateAccountRequestParameterizedConstructor() {
        UUID userId = UUID.randomUUID();
        String name = "Investment Account";
        String type = "INVESTMENT";

        // 1. Exercita o construtor completo e os getters
        CreateAccountRequest request = new CreateAccountRequest(name, userId, type);

        assertEquals(name, request.getName());
        assertEquals(userId, request.getUserId());
        assertEquals(type, request.getType());
    }

    @Test
    @DisplayName("Should cover the empty constructor and all setters/getters")
    void testCreateAccountRequestEmptyConstructorAndSetters() {
        UUID userId = UUID.randomUUID();
        String name = "Checking Account";
        String type = "CHECKING";

        // 2. Exercita o construtor vazio (usado pelo Jackson/Spring)
        CreateAccountRequest request = new CreateAccountRequest();

        // 3. Exercita todos os setters manuais para zerar as linhas vermelhas
        request.setName(name);
        request.setUserId(userId);
        request.setType(type);

        // 4. Valida se os dados foram injetados corretamente
        assertEquals(name, request.getName());
        assertEquals(userId, request.getUserId());
        assertEquals(type, request.getType());
    }
}