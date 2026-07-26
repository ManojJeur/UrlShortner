package com.project.urlshortener.exception;

public class ConflictException extends BaseApplicationException {

    public ConflictException(String message, String errorCode) {
        super(message, errorCode);
    }
}
