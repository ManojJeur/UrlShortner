package com.project.urlshortener.exception;

public class BadRequestException extends BaseApplicationException {

    public BadRequestException(String message, String errorCode) {
        super(message, errorCode);
    }
}
