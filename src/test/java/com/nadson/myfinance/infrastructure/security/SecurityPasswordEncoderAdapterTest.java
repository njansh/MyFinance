package com.nadson.myfinance.infrastructure.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SecurityPasswordEncoderAdapterTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private SecurityPasswordEncoderAdapter adapter;

    @Test
    @DisplayName("Should delegate password encoding to Spring Security core")
    void shouldEncodePassword() {
        String rawPassword = "secretPassword123";
        String encodedPassword = "encoded_hash_here";

        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

        String result = adapter.encode(rawPassword);

        assertEquals(encodedPassword, result);
        verify(passwordEncoder).encode(rawPassword);
    }
}