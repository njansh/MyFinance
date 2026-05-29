package com.nadson.myfinance.infrastructure.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

public class BeanConfigurationTest {

    @Test
    @DisplayName("Deve instanciar todos os beans de UseCases e Ports dinamicamente")
    void shouldInstantiateAllBeans() throws Exception {
        BeanConfiguration config = new BeanConfiguration();
        Method[] methods = BeanConfiguration.class.getDeclaredMethods();

        for (Method method : methods) {
            // Ignora métodos internos do Java ou que não retornam objetos
            if (method.getReturnType() != void.class && !method.isSynthetic()) {

                // Cria um Mock para cada parâmetro que o método do BeanConfiguration exigir
                Object[] mocks = new Object[method.getParameterCount()];
                for (int i = 0; i < method.getParameterCount(); i++) {
                    mocks[i] = mock(method.getParameterTypes()[i]);
                }

                // Invoca o método dinamicamente passando os mocks
                Object result = method.invoke(config, mocks);

                // Garante que o Bean foi criado
                assertNotNull(result, "O Bean falhou ao ser criado no método: " + method.getName());
            }
        }
    }
}