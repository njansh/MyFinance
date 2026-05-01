package com.nadson.myfinance.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Desabilita CSRF para que o POST do Swagger não seja bloqueado com 403
                .csrf(csrf -> csrf.disable())

                // 2. Define as regras de acesso
                .authorizeHttpRequests(auth -> auth
                        // Libera o acesso visual ao Swagger e à documentação
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        // Exige autenticação para qualquer outra rota (como /transactions)
                        .anyRequest().authenticated()
                )

                // 3. Habilita o formulário de login e o suporte a login básico
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}