package com.project.urlshortener.exception;

public class ServiceUnavailableException extends BaseApplicationException {

    public ServiceUnavailableException(String message, String errorCode) {
        super(message, errorCode);
    }
}
