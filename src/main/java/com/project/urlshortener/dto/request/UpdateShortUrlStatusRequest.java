package com.project.urlshortener.dto.request;

import java.util.Objects;

public class UpdateShortUrlStatusRequest {

    private boolean enabled;

    public UpdateShortUrlStatusRequest() {
    }

    public UpdateShortUrlStatusRequest(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UpdateShortUrlStatusRequest that = (UpdateShortUrlStatusRequest) o;
        return enabled == that.enabled;
    }

    @Override
    public int hashCode() {
        return Objects.hash(enabled);
    }

    @Override
    public String toString() {
        return "UpdateShortUrlStatusRequest{" +
                "enabled=" + enabled +
                '}';
    }
}
