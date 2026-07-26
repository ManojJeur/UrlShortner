package com.project.urlshortener.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void applicationExceptionShouldMapToBadRequest() {
        ResponseEntity<?> response = handler.handleApplicationException(
                new BadRequestException("Alias already exists", "ALIAS_EXISTS")
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Object body = response.getBody();
        assertFalse(((com.project.urlshortener.response.ErrorResponse) body).isSuccess());
    }
}
