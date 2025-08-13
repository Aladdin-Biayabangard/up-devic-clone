package com.team.updevic001.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MM yyyy HH:mm");

    private String getFormattedDate() {
        return LocalDateTime.now().format(formatter);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handler(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            if (error instanceof FieldError) {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            } else {
                errors.put("error", "Invalid input");
            }
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(ResourceAlreadyExistException.class)
    public ResponseEntity<ErrorResponse> handleException(ResourceAlreadyExistException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(UnauthorizedException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(ExpiredRefreshTokenException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ExpiredRefreshTokenException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handlerForbiddenException(ForbiddenException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handlerForbiddenException(AlreadyExistsException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(PaymentStatusException.class)
    public ResponseEntity<ErrorResponse> handlerPaymentException(PaymentStatusException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.PAYMENT_REQUIRED, request);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(String message, HttpStatus httpStatus, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                httpStatus.value(),
                message,
                request.getDescription(false),
                getFormattedDate()
        );
        return new ResponseEntity<>(errorResponse, httpStatus);
    }
}
