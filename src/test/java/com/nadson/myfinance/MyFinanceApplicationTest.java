package com.nadson.myfinance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.mockito.Mockito.mockStatic;

public class MyFinanceApplicationTest {

    @Test
    @DisplayName("Deve cobrir o método main da aplicação")
    void shouldRunMainMethod() {
        try (MockedStatic<SpringApplication> mockedSpring = mockStatic(SpringApplication.class)) {

            MyFinanceApplication.main(new String[]{});

            mockedSpring.verify(() -> SpringApplication.run(MyFinanceApplication.class, new String[]{}));
        }
    }
}