package com.nadson.myfinance.infrastructure.adapter.web.handler;

import com.nadson.myfinance.domain.exception.BusinessRuleException;
import com.nadson.myfinance.domain.exception.ResourceNotFoundException;
import com.nadson.myfinance.domain.exception.UserAlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldReturnFormattedProblemDetailWhenResourceNotFound() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Not found");

        ProblemDetail response = handler.handleResourceNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertEquals("Not found", response.getDetail());
        assertEquals("Recurso não encontrado", response.getTitle());
    }

    @Test
    void shouldReturnBadRequestWhenBusinessRuleExceptionOccurs() {
        BusinessRuleException ex = new BusinessRuleException("Regra violada");

        ProblemDetail response = handler.handleBadRequest(ex);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals("Regra violada", response.getDetail());
        assertEquals("Requisição Inválida", response.getTitle());
    }

    @Test
    void shouldReturnConflictWhenUserAlreadyExists() {
        UserAlreadyExistsException ex = new UserAlreadyExistsException("Usuário já cadastrado");

        ProblemDetail response = handler.handleConflict(ex);

        assertEquals(HttpStatus.CONFLICT.value(), response.getStatus());
        assertEquals("Usuário já cadastrado", response.getDetail());
        assertEquals("Conflito de dados", response.getTitle());
    }

    @Test
    void shouldReturnInternalServerErrorForGenericException() {
        Exception ex = new RuntimeException("Unexpected");

        ProblemDetail response = handler.handleGenericException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());
        assertEquals("An unexpected error occurred. Please contact support.", response.getDetail());
        assertEquals("Erro Interno do Servidor", response.getTitle());
    }
}
