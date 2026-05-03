package com.nadson.myfinance.infrastructure.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SecurityConfigTest.LocalTestController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = true) // <--- FORÇA a ativação dos filtros de segurança
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtAuthenticationFilter jwtAuthFilter;

    @MockBean
    private StringRedisTemplate stringRedisTemplate;


    @Test
    @DisplayName("Deve permitir acesso (200) para rotas públicas")
    void shouldReturn200ForPublicRoute() throws Exception {
        mockMvc.perform(post("/users/register"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve permitir acesso (200) quando o usuário estiver logado")
    void shouldReturn200WhenAuthenticated() throws Exception {
        mockMvc.perform(get("/test/protected"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @RestController
    static class LocalTestController {
        @PostMapping("/users/register")
        public void publicRoute() {}

        @GetMapping("/test/protected")
        public void protectedRoute() {}
    }
}