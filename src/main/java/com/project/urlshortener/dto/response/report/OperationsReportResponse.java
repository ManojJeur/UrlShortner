package com.project.urlshortener.dto.response.report;

import java.time.Instant;
import java.util.Objects;

public class OperationsReportResponse {

    private Instant generatedAt;
    private long totalUsers;
    private long totalShortUrls;
    private long activeShortUrls;
    private long expiredShortUrls;
    private long totalClickEvents;
    private long totalRefreshTokens;
    private long activeRefreshTokens;
    private long revokedRefreshTokens;
    private long expiredRefreshTokens;

    public OperationsReportResponse() {
    }

    public OperationsReportResponse(Instant generatedAt,
                                    long totalUsers,
                                    long totalShortUrls,
                                    long activeShortUrls,
                                    long expiredShortUrls,
                                    long totalClickEvents,
                                    long totalRefreshTokens,
                                    long activeRefreshTokens,
                                    long revokedRefreshTokens,
                                    long expiredRefreshTokens) {
        this.generatedAt = generatedAt;
        this.totalUsers = totalUsers;
        this.totalShortUrls = totalShortUrls;
        this.activeShortUrls = activeShortUrls;
        this.expiredShortUrls = expiredShortUrls;
        this.totalClickEvents = totalClickEvents;
        this.totalRefreshTokens = totalRefreshTokens;
        this.activeRefreshTokens = activeRefreshTokens;
        this.revokedRefreshTokens = revokedRefreshTokens;
        this.expiredRefreshTokens = expiredRefreshTokens;
    }

    public Instant getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(Instant generatedAt) {
        this.generatedAt = generatedAt;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalShortUrls() {
        return totalShortUrls;
    }

    public void setTotalShortUrls(long totalShortUrls) {
        this.totalShortUrls = totalShortUrls;
    }

    public long getActiveShortUrls() {
        return activeShortUrls;
    }

    public void setActiveShortUrls(long activeShortUrls) {
        this.activeShortUrls = activeShortUrls;
    }

    public long getExpiredShortUrls() {
        return expiredShortUrls;
    }

    public void setExpiredShortUrls(long expiredShortUrls) {
        this.expiredShortUrls = expiredShortUrls;
    }

    public long getTotalClickEvents() {
        return totalClickEvents;
    }

    public void setTotalClickEvents(long totalClickEvents) {
        this.totalClickEvents = totalClickEvents;
    }

    public long getTotalRefreshTokens() {
        return totalRefreshTokens;
    }

    public void setTotalRefreshTokens(long totalRefreshTokens) {
        this.totalRefreshTokens = totalRefreshTokens;
    }

    public long getActiveRefreshTokens() {
        return activeRefreshTokens;
    }

    public void setActiveRefreshTokens(long activeRefreshTokens) {
        this.activeRefreshTokens = activeRefreshTokens;
    }

    public long getRevokedRefreshTokens() {
        return revokedRefreshTokens;
    }

    public void setRevokedRefreshTokens(long revokedRefreshTokens) {
        this.revokedRefreshTokens = revokedRefreshTokens;
    }

    public long getExpiredRefreshTokens() {
        return expiredRefreshTokens;
    }

    public void setExpiredRefreshTokens(long expiredRefreshTokens) {
        this.expiredRefreshTokens = expiredRefreshTokens;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OperationsReportResponse that = (OperationsReportResponse) o;
        return totalUsers == that.totalUsers
                && totalShortUrls == that.totalShortUrls
                && activeShortUrls == that.activeShortUrls
                && expiredShortUrls == that.expiredShortUrls
                && totalClickEvents == that.totalClickEvents
                && totalRefreshTokens == that.totalRefreshTokens
                && activeRefreshTokens == that.activeRefreshTokens
                && revokedRefreshTokens == that.revokedRefreshTokens
                && expiredRefreshTokens == that.expiredRefreshTokens
                && Objects.equals(generatedAt, that.generatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(generatedAt, totalUsers, totalShortUrls, activeShortUrls, expiredShortUrls, totalClickEvents, totalRefreshTokens, activeRefreshTokens, revokedRefreshTokens, expiredRefreshTokens);
    }

    @Override
    public String toString() {
        return "OperationsReportResponse{" +
                "generatedAt=" + generatedAt +
                ", totalUsers=" + totalUsers +
                ", totalShortUrls=" + totalShortUrls +
                ", activeShortUrls=" + activeShortUrls +
                ", expiredShortUrls=" + expiredShortUrls +
                ", totalClickEvents=" + totalClickEvents +
                ", totalRefreshTokens=" + totalRefreshTokens +
                ", activeRefreshTokens=" + activeRefreshTokens +
                ", revokedRefreshTokens=" + revokedRefreshTokens +
                ", expiredRefreshTokens=" + expiredRefreshTokens +
                '}';
    }
}
