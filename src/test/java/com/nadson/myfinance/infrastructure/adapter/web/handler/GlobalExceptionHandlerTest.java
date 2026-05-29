package com.nadson.myfinance.infrastructure.adapter.web.handler;

import com.nadson.myfinance.domain.exception.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Deve tratar ResourceNotFoundException e retornar 404")
    void handleResourceNotFound() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Entidade não encontrada");
        ProblemDetail response = handler.handleResourceNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertEquals("Entidade não encontrada", response.getDetail());
        assertEquals("Recurso não encontrado", response.getTitle());
    }

    @Test
    @DisplayName("Deve tratar BusinessRuleException (e similares) e retornar 400")
    void handleBadRequest() {
        BusinessRuleException ex = new BusinessRuleException("Regra violada");
        ProblemDetail response = handler.handleBadRequest(ex);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals("Regra violada", response.getDetail());
        assertEquals("Requisição Inválida", response.getTitle());
    }

    @Test
    @DisplayName("Deve tratar UserAlreadyExistsException (e similares) e retornar 409")
    void handleConflict() {
        UserAlreadyExistsException ex = new UserAlreadyExistsException("Usuário já existe");
        ProblemDetail response = handler.handleConflict(ex);

        assertEquals(HttpStatus.CONFLICT.value(), response.getStatus());
        assertEquals("Usuário já existe", response.getDetail());
        assertEquals("Conflito de dados", response.getTitle());
    }

    @Test
    @DisplayName("Deve tratar MethodArgumentNotValidException e retornar 400 com propriedades de validação")
    void handleValidationErrors() {
        // Criando mocks para simular a exceção de validação do Spring
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("objeto", "email", "deve ser um e-mail válido");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ProblemDetail response = handler.handleValidationErrors(ex);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals("Erro de Validação", response.getTitle());
        assertTrue(response.getProperties().containsKey("invalid_params"));
        assertEquals("email: deve ser um e-mail válido", response.getProperties().get("invalid_params"));
    }

    @Test
    @DisplayName("Deve tratar Exception genérica e retornar 500")
    void handleGenericException() {
        Exception ex = new Exception("Erro catastrófico no banco");
        ProblemDetail response = handler.handleGenericException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());
        assertEquals("An unexpected error occurred. Please contact support.", response.getDetail());
        assertEquals("Erro Interno do Servidor", response.getTitle());
    }

    @Test
    @DisplayName("Deve tratar ObjectOptimisticLockingFailureException e retornar 409")
    void handleOptimisticLockingFailure() {
        ObjectOptimisticLockingFailureException ex = new ObjectOptimisticLockingFailureException("Account", 1);
        ProblemDetail response = handler.handleOptimisticLockingFailure(ex);

        assertEquals(HttpStatus.CONFLICT.value(), response.getStatus());
        assertEquals("Conflito de Concorrência", response.getTitle());
        assertTrue(response.getDetail().contains("modificada por outra operação simultânea"));
    }

    @Test
    @DisplayName("Deve tratar BudgetAlertException e retornar 409 com alert_type")
    void handleBudgetAlert() {
        // Mock da exceção de domínio para não dependermos do construtor exato ou de Enums complexos
        BudgetAlertException ex = mock(BudgetAlertException.class);
        when(ex.getMessage()).thenReturn("Orçamento estourou");
        // O Mockito retornará null por padrão para o getAlertType(), o que é suficiente para validar a lógica de inserção na propriedade

        ProblemDetail response = handler.handleBudgetAlert(ex);

        assertEquals(HttpStatus.CONFLICT.value(), response.getStatus());
        assertEquals("Alerta de Orçamento", response.getTitle());
        assertEquals("Orçamento estourou", response.getDetail());
        assertTrue(response.getProperties().containsKey("alert_type"));
    }
}