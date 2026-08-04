package com.project.urlshortener.dto.request;

import java.util.Objects;

public class ShortUrlSearchRequest {

    private String query;
    private Boolean enabled;
    private Boolean expired;
    private Boolean hasCustomAlias;
    private int page;
    private int size;
    private String sortBy;
    private String sortDirection;

    public ShortUrlSearchRequest() {
    }

    public ShortUrlSearchRequest(String query, Boolean enabled, Boolean expired, Boolean hasCustomAlias, int page, int size, String sortBy, String sortDirection) {
        this.query = query;
        this.enabled = enabled;
        this.expired = expired;
        this.hasCustomAlias = hasCustomAlias;
        this.page = page;
        this.size = size;
        this.sortBy = sortBy;
        this.sortDirection = sortDirection;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getExpired() {
        return expired;
    }

    public void setExpired(Boolean expired) {
        this.expired = expired;
    }

    public Boolean getHasCustomAlias() {
        return hasCustomAlias;
    }

    public void setHasCustomAlias(Boolean hasCustomAlias) {
        this.hasCustomAlias = hasCustomAlias;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ShortUrlSearchRequest that = (ShortUrlSearchRequest) o;
        return page == that.page
                && size == that.size
                && Objects.equals(query, that.query)
                && Objects.equals(enabled, that.enabled)
                && Objects.equals(expired, that.expired)
                && Objects.equals(hasCustomAlias, that.hasCustomAlias)
                && Objects.equals(sortBy, that.sortBy)
                && Objects.equals(sortDirection, that.sortDirection);
    }

    @Override
    public int hashCode() {
        return Objects.hash(query, enabled, expired, hasCustomAlias, page, size, sortBy, sortDirection);
    }

    @Override
    public String toString() {
        return "ShortUrlSearchRequest{" +
                "query='" + query + '\'' +
                ", enabled=" + enabled +
                ", expired=" + expired +
                ", hasCustomAlias=" + hasCustomAlias +
                ", page=" + page +
                ", size=" + size +
                ", sortBy='" + sortBy + '\'' +
                ", sortDirection='" + sortDirection + '\'' +
                '}';
    }
}
