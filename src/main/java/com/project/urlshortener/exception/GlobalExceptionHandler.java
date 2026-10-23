package com.project.urlshortener.exception;

import com.project.urlshortener.response.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.validation.BindException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BaseApplicationException.class)
    public ResponseEntity<ErrorResponse> handleApplicationException(BaseApplicationException exception) {
        log.warn("Application error [{}]", exception.getErrorCode());
        ErrorResponse errorResponse = ErrorResponse.of(exception.getMessage(), exception.getErrorCode());
        HttpStatus status = exception instanceof ResourceNotFoundException
                ? HttpStatus.NOT_FOUND
                : exception instanceof ConflictException
                ? HttpStatus.CONFLICT
                : exception instanceof ServiceUnavailableException
                ? HttpStatus.SERVICE_UNAVAILABLE
                : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(BindException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": "
                        + (fieldError.getDefaultMessage() == null ? "invalid value" : fieldError.getDefaultMessage()))
                .collect(Collectors.joining(", "));
        if (message.isBlank()) {
            message = "Request validation failed";
        }

        log.warn("Request validation failed");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(message, "VALIDATION_ERROR"));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException exception) {
        String message = exception.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(message.isBlank() ? "Request validation failed" : message, "VALIDATION_ERROR"));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException exception) {
        log.warn("Authentication failed");
        return error(HttpStatus.UNAUTHORIZED, "Invalid email or password", "INVALID_CREDENTIALS");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException exception) {
        return error(HttpStatus.FORBIDDEN, "You do not have permission to perform this action", "FORBIDDEN");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableBody(HttpMessageNotReadableException exception) {
        return error(HttpStatus.BAD_REQUEST, "Request body is missing or contains invalid JSON", "INVALID_REQUEST_BODY");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException exception) {
        return error(HttpStatus.BAD_REQUEST, "Invalid value for parameter '" + exception.getName() + "'", "INVALID_PARAMETER");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(MissingServletRequestParameterException exception) {
        return error(HttpStatus.BAD_REQUEST, "Required parameter '" + exception.getParameterName() + "' is missing", "MISSING_PARAMETER");
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponse> handleMissingHeader(MissingRequestHeaderException exception) {
        return error(HttpStatus.BAD_REQUEST, "Required header '" + exception.getHeaderName() + "' is missing", "MISSING_HEADER");
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> handleMethodValidation(HandlerMethodValidationException exception) {
        return error(HttpStatus.BAD_REQUEST, "Request parameters failed validation", "VALIDATION_ERROR");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotAllowed(HttpRequestMethodNotSupportedException exception) {
        return error(HttpStatus.METHOD_NOT_ALLOWED, "HTTP method is not supported for this endpoint", "METHOD_NOT_ALLOWED");
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException exception) {
        return error(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Request content type is not supported", "UNSUPPORTED_MEDIA_TYPE");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFound(NoResourceFoundException exception) {
        return error(HttpStatus.NOT_FOUND, "The requested endpoint was not found", "ENDPOINT_NOT_FOUND");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception exception) {
        log.error("Unexpected error", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of("An unexpected error occurred", "INTERNAL_SERVER_ERROR"));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException exception) {
        log.warn("Data integrity violation");
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse.of("A record with the same unique value already exists", "CONFLICT"));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDataAccessException(DataAccessException exception) {
        log.error("Data service unavailable", exception);
        return error(HttpStatus.SERVICE_UNAVAILABLE, "A required data service is temporarily unavailable", "SERVICE_UNAVAILABLE");
    }

    private ResponseEntity<ErrorResponse> error(HttpStatus status, String message, String code) {
        return ResponseEntity.status(status).body(ErrorResponse.of(message, code));
    }
}
