package com.nadson.myfinance.application.usecase;

import com.nadson.myfinance.application.port.in.CreateCategoryPort;
import com.nadson.myfinance.application.port.out.UserRepositoryPort;
import com.nadson.myfinance.domain.entity.User;
import com.nadson.myfinance.domain.enums.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateUserUseCaseTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private CreateCategoryPort createCategoryPort;

    @InjectMocks
    private CreateUserUseCase useCase;

    @Test
    @DisplayName("Deve criar usuário e todas as categorias padrão")
    void shouldCreateUserAndDefaultCategories() {
        String name = "Nadson Jhony";
        String email = "nadson@example.com";
        UUID generatedId = UUID.randomUUID();
        User savedUser = new User(generatedId, name, email);

        when(userRepositoryPort.save(any(User.class))).thenReturn(savedUser);

        User result = useCase.execute(name, email);

        assertNotNull(result);
        assertEquals(generatedId, result.getId());
        assertEquals(email, result.getEmail());

        verify(userRepositoryPort, times(1)).save(any(User.class));

        verify(createCategoryPort).execute(eq(generatedId), eq("Salário"), anyString(), eq(TransactionType.INCOME));
        verify(createCategoryPort).execute(eq(generatedId), eq("Renda Extra"), anyString(), eq(TransactionType.INCOME));
        verify(createCategoryPort).execute(eq(generatedId), eq("Investimentos"), anyString(), eq(TransactionType.INCOME));

        verify(createCategoryPort).execute(eq(generatedId), eq("Alimentação"), anyString(), eq(TransactionType.EXPENSE));
        verify(createCategoryPort).execute(eq(generatedId), eq("Moradia"), anyString(), eq(TransactionType.EXPENSE));
        verify(createCategoryPort).execute(eq(generatedId), eq("Transporte"), anyString(), eq(TransactionType.EXPENSE));

        verify(createCategoryPort, times(10)).execute(eq(generatedId), anyString(), anyString(), any(TransactionType.class));
    }
}