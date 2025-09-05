package com.team.updevic001.exceptions;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import static com.team.updevic001.model.enums.ExceptionConstants.UNEXPECTED_EXCEPTION;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(Exception.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ErrorResponse handle(Exception ex) {
        log.error("Exception: " + ex);
        return new ErrorResponse(UNEXPECTED_EXCEPTION.getCode(), UNEXPECTED_EXCEPTION.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage())
        );

        Map<String, Object> response = new HashMap<>();
        response.put("code", "VALIDATION_FAILED");
        response.put("message", "Validation failed");
        response.put("errors", fieldErrors);

        return ResponseEntity.badRequest().body(response);
    }


    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ErrorResponse handle(NotFoundException ex) {
        log.error("Not found exception: " + ex);
        return new ErrorResponse(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(UNAUTHORIZED)
    public ErrorResponse handle(UnauthorizedException ex) {
        log.error("Unauthorized exception: ", ex);
        return new ErrorResponse(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(FORBIDDEN)
    public ErrorResponse handle(ForbiddenException ex) {
        log.error("Forbidden exception: ", ex);
        return new ErrorResponse(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler(AlreadyExistsException.class)
    @ResponseStatus(CONFLICT)
    public ErrorResponse handle(AlreadyExistsException ex) {
        log.error("Already exception: ", ex);
        return new ErrorResponse(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler(ExpiredRefreshTokenException.class)
    @ResponseStatus(UNAUTHORIZED)
    public ErrorResponse handle(ExpiredRefreshTokenException ex) {
        log.error("Expired refresh token exception: ", ex);
        return new ErrorResponse(ex.getCode(), ex.getMessage());
    }
//
//    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MM yyyy HH:mm");
//
//    private String getFormattedDate() {
//        return LocalDateTime.now().format(formatter);
//    }
//
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<Map<String, String>> handler(MethodArgumentNotValidException ex) {
//        Map<String, String> errors = new HashMap<>();
//        ex.getBindingResult().getAllErrors().forEach((error) -> {
//            if (error instanceof FieldError) {
//                String fieldName = ((FieldError) error).getField();
//                String errorMessage = error.getDefaultMessage();
//                errors.put(fieldName, errorMessage);
//            } else {
//                errors.put("error", "Invalid input");
//            }
//        });
//        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ErrorResponse> handleException(Exception ex, WebRequest request) {
//        return buildErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request);
//    }
//
//    @ExceptionHandler(ResourceAlreadyExistException.class)
//    public ResponseEntity<ErrorResponse> handleException(ResourceAlreadyExistException ex, WebRequest request) {
//        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT, request);
//    }
//
//    @ExceptionHandler(IllegalArgumentException.class)
//    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
//        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, request);
//    }
//
//    @ExceptionHandler(ResourceNotFoundException.class)
//    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
//        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request);
//    }
//
//    @ExceptionHandler(UnauthorizedException.class)
//    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(UnauthorizedException ex, WebRequest request) {
//        return buildErrorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED, request);
//    }
//
//    @ExceptionHandler(ExpiredRefreshTokenException.class)
//    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ExpiredRefreshTokenException ex, WebRequest request) {
//        return buildErrorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED, request);
//    }
//
//    @ExceptionHandler(ForbiddenException.class)
//    public ResponseEntity<ErrorResponse> handlerForbiddenException(ForbiddenException ex, WebRequest request) {
//        return buildErrorResponse(ex.getMessage(), HttpStatus.FORBIDDEN, request);
//    }
//
//    @ExceptionHandler(AlreadyExistsException.class)
//    public ResponseEntity<ErrorResponse> handlerForbiddenException(AlreadyExistsException ex, WebRequest request) {
//        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT, request);
//    }
//
//    @ExceptionHandler(PaymentStatusException.class)
//    public ResponseEntity<ErrorResponse> handlerPaymentException(PaymentStatusException ex, WebRequest request) {
//        return buildErrorResponse(ex.getMessage(), HttpStatus.PAYMENT_REQUIRED, request);
//    }
//
//    private ResponseEntity<ErrorResponse> buildErrorResponse(String message, HttpStatus httpStatus, WebRequest request) {
//        ErrorResponse errorResponse = new ErrorResponse(
//                httpStatus.value(),
//                message,
//                request.getDescription(false),
//                getFormattedDate()
//        );
//        return new ResponseEntity<>(errorResponse, httpStatus);
//    }
}
