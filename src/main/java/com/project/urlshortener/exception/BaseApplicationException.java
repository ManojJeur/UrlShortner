package com.project.urlshortener.exception;

public abstract class BaseApplicationException extends RuntimeException {

    private final String errorCode;

    protected BaseApplicationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
