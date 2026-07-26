package com.project.urlshortener.response;

import java.time.Instant;
import java.util.Objects;

public class ApiResponse<T> {

    private final boolean success;
    private final String message;
    private final Instant timestamp;
    private final T data;

    public ApiResponse(boolean success, String message, Instant timestamp, T data) {
        this.success = success;
        this.message = message;
        this.timestamp = timestamp;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, Instant.now(), data);
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message, Instant.now(), null);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public T getData() {
        return data;
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                ", data=" + data +
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
        ApiResponse<?> that = (ApiResponse<?>) o;
        return success == that.success
                && Objects.equals(message, that.message)
                && Objects.equals(timestamp, that.timestamp)
                && Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(success, message, timestamp, data);
    }
}
