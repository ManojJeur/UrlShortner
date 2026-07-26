package com.project.urlshortener.response;

import java.time.Instant;
import java.util.Objects;

public class ErrorResponse {

    private final boolean success;
    private final String message;
    private final String errorCode;
    private final Instant timestamp;

    public ErrorResponse(boolean success, String message, String errorCode, Instant timestamp) {
        this.success = success;
        this.message = message;
        this.errorCode = errorCode;
        this.timestamp = timestamp;
    }

    public static ErrorResponse of(String message, String errorCode) {
        return new ErrorResponse(false, message, errorCode, Instant.now());
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", errorCode='" + errorCode + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ErrorResponse that = (ErrorResponse) o;
        return success == that.success
                && Objects.equals(message, that.message)
                && Objects.equals(errorCode, that.errorCode)
                && Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(success, message, errorCode, timestamp);
    }
}
