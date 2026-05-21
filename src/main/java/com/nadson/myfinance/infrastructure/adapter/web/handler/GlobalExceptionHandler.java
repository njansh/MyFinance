package com.nadson.myfinance.infrastructure.adapter.web.handler;

import com.nadson.myfinance.domain.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. RECURSOS NÃO ENCONTRADOS (404)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFound(ResourceNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Recurso não encontrado");
        return problemDetail;
    }

    // 2. REGRAS DE NEGÓCIO E VALORES INVÁLIDOS (400)
    @ExceptionHandler({
            BusinessRuleException.class,
            InvalidTransactionValueException.class,
            IllegalArgumentException.class,
            IllegalStateException.class
    })
    public ProblemDetail handleBadRequest(RuntimeException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Requisição Inválida");
        return problemDetail;
    }

    // 3. CONFLITOS DE DADOS (409)
    @ExceptionHandler({
            UserAlreadyExistsException.class,
            DuplicateResourceException.class
    })
    public ProblemDetail handleConflict(RuntimeException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problemDetail.setTitle("Conflito de dados");
        return problemDetail;
    }

    // 4. ERROS DE VALIDAÇÃO DE CAMPOS (@Valid) (400)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationErrors(MethodArgumentNotValidException ex) {
        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
        problemDetail.setTitle("Erro de Validação");
        problemDetail.setProperty("invalid_params", details);
        return problemDetail;
    }

    // 5. ERRO GENÉRICO DO SERVIDOR (500)
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR, 
                "An unexpected error occurred. Please contact support."
        );
        problemDetail.setTitle("Erro Interno do Servidor");
        return problemDetail;
    }
    

    // 6. FALHAS DE CONCORRÊNCIA E TRANSAÇÃO (409)
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ProblemDetail handleOptimisticLockingFailure(ObjectOptimisticLockingFailureException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                "A conta ou transação foi modificada por outra operação simultânea. Por favor, tente novamente."
        );
        problemDetail.setTitle("Conflito de Concorrência");
        return problemDetail;
    }

    // 7. ALERTAS DE ORÇAMENTO (409)
    @ExceptionHandler(BudgetAlertException.class)
    public ProblemDetail handleBudgetAlert(BudgetAlertException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT, // 409 indica que a operação ocorreu, mas há um conflito com a meta
                ex.getMessage()
        );
        problemDetail.setTitle("Alerta de Orçamento");
        problemDetail.setProperty("alert_type", ex.getAlertType());
        return problemDetail;
    }
}
