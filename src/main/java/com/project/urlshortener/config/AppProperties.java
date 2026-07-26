package com.project.urlshortener.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Objects;

@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private String timezone = "UTC";
    private String apiBasePath = "/api/v1";

    public AppProperties() {
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getApiBasePath() {
        return apiBasePath;
    }

    public void setApiBasePath(String apiBasePath) {
        this.apiBasePath = apiBasePath;
    }

    @Override
    public String toString() {
        return "AppProperties{" +
                "timezone='" + timezone + '\'' +
                ", apiBasePath='" + apiBasePath + '\'' +
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
        AppProperties that = (AppProperties) o;
        return Objects.equals(timezone, that.timezone) && Objects.equals(apiBasePath, that.apiBasePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timezone, apiBasePath);
    }
}
