package org.testcompany.customerrewards.config;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.testcompany.customerrewards.dto.Error;
import org.testcompany.customerrewards.exceptions.CustomerRewardsValidationException;

import java.util.Objects;

@RestControllerAdvice
public class CustomerRewardsExceptionHandler {
    private static final Logger logger =
            LoggerFactory.getLogger(CustomerRewardsExceptionHandler.class);

    @ExceptionHandler
    public ResponseEntity<Error> handleCustomRewardsValidationException(
            CustomerRewardsValidationException ex) {
        logger.error("Invalid request for calculating customer rewards points", ex);
        return ResponseEntity.badRequest().body(new Error(ex.getMessage(),
                HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler
    public ResponseEntity<Error> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex) {
        logger.error("Invalid request", ex);
        return ResponseEntity.badRequest().body(new Error(
                ex.getFieldErrors().get(0).getDefaultMessage(),
                HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler
    public ResponseEntity<Error> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex) {
        logger.error("Invalid request", ex);
        return ResponseEntity.badRequest().body(new Error(
                "Invalid request param (".concat(Objects.requireNonNull(ex.getParameter().getParameterName())
                                .concat(") found")),
                HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler
    public ResponseEntity<Error> handleIllegalArgumentException(
            IllegalArgumentException ex) {
        logger.error("Invalid request", ex);
        return ResponseEntity.badRequest().body(new Error(
                "Invalid request.",
                HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler
    public ResponseEntity<Error> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex) {
        logger.error("Invalid request", ex);
        return ResponseEntity.badRequest().body(new Error(
                "Invalid request. Please ensure all the request fields are in correct format.",
                HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler
    public ResponseEntity<Error> handleConstraintViolationException(
            ConstraintViolationException ex) {
        logger.error("Invalid request", ex);
        return ResponseEntity.badRequest().body(new Error(
                ex.getConstraintViolations().stream().findFirst().orElseThrow().getMessage(),
                HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler
    public ResponseEntity<Void> handleNoResourceFoundException(
            NoResourceFoundException ex) {
        logger.error("Invalid request", ex);
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler
    public ResponseEntity<Error> handleException(Exception ex) {
        logger.error("Internal error occurred.", ex);
        return ResponseEntity.internalServerError().body(new Error("Internal " +
                "server error",
                HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }
}
