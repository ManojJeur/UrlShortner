package com.project.urlshortener.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Objects;

@ConfigurationProperties(prefix = "app.short-url")
public class ShortUrlProperties {

    private String baseUrl;
    private int codeLength = 7;
    private int generationAttempts = 10;

    public ShortUrlProperties() {
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public int getCodeLength() {
        return codeLength;
    }

    public void setCodeLength(int codeLength) {
        this.codeLength = codeLength;
    }

    public int getGenerationAttempts() {
        return generationAttempts;
    }

    public void setGenerationAttempts(int generationAttempts) {
        this.generationAttempts = generationAttempts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ShortUrlProperties that = (ShortUrlProperties) o;
        return codeLength == that.codeLength
                && generationAttempts == that.generationAttempts
                && Objects.equals(baseUrl, that.baseUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseUrl, codeLength, generationAttempts);
    }

    @Override
    public String toString() {
        return "ShortUrlProperties{" +
                "baseUrl='" + baseUrl + '\'' +
                ", codeLength=" + codeLength +
                ", generationAttempts=" + generationAttempts +
                '}';
    }
}
