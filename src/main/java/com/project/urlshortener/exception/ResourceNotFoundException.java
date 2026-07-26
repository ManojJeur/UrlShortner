package com.project.urlshortener.exception;

public class ResourceNotFoundException extends BaseApplicationException {

    public ResourceNotFoundException(String message, String errorCode) {
        super(message, errorCode);
    }
}
