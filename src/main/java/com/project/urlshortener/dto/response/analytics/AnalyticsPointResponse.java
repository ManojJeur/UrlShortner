package com.project.urlshortener.dto.response.analytics;

import java.util.Objects;

public class AnalyticsPointResponse {

    private String label;
    private long count;

    public AnalyticsPointResponse() {
    }

    public AnalyticsPointResponse(String label, long count) {
        this.label = label;
        this.count = count;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AnalyticsPointResponse that = (AnalyticsPointResponse) o;
        return count == that.count && Objects.equals(label, that.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label, count);
    }

    @Override
    public String toString() {
        return "AnalyticsPointResponse{" +
                "label='" + label + '\'' +
                ", count=" + count +
                '}';
    }
}
