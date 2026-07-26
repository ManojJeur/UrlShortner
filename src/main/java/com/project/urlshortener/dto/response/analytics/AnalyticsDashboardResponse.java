package com.project.urlshortener.dto.response.analytics;

import java.util.List;
import java.util.Objects;

public class AnalyticsDashboardResponse {

    private long totalClicks;
    private long uniqueShortUrls;
    private List<AnalyticsPointResponse> dailyClicks;
    private List<AnalyticsPointResponse> weeklyClicks;
    private List<AnalyticsPointResponse> monthlyClicks;
    private List<AnalyticsMetricResponse> topCountries;
    private List<AnalyticsMetricResponse> topBrowsers;
    private List<AnalyticsMetricResponse> topDevices;
    private List<AnalyticsMetricResponse> topUrls;
    private List<AnalyticsMetricResponse> mostActiveUsers;

    public AnalyticsDashboardResponse() {
    }

    public AnalyticsDashboardResponse(long totalClicks,
                                      long uniqueShortUrls,
                                      List<AnalyticsPointResponse> dailyClicks,
                                      List<AnalyticsPointResponse> weeklyClicks,
                                      List<AnalyticsPointResponse> monthlyClicks,
                                      List<AnalyticsMetricResponse> topCountries,
                                      List<AnalyticsMetricResponse> topBrowsers,
                                      List<AnalyticsMetricResponse> topDevices,
                                      List<AnalyticsMetricResponse> topUrls,
                                      List<AnalyticsMetricResponse> mostActiveUsers) {
        this.totalClicks = totalClicks;
        this.uniqueShortUrls = uniqueShortUrls;
        this.dailyClicks = dailyClicks;
        this.weeklyClicks = weeklyClicks;
        this.monthlyClicks = monthlyClicks;
        this.topCountries = topCountries;
        this.topBrowsers = topBrowsers;
        this.topDevices = topDevices;
        this.topUrls = topUrls;
        this.mostActiveUsers = mostActiveUsers;
    }

    public long getTotalClicks() {
        return totalClicks;
    }

    public void setTotalClicks(long totalClicks) {
        this.totalClicks = totalClicks;
    }

    public long getUniqueShortUrls() {
        return uniqueShortUrls;
    }

    public void setUniqueShortUrls(long uniqueShortUrls) {
        this.uniqueShortUrls = uniqueShortUrls;
    }

    public List<AnalyticsPointResponse> getDailyClicks() {
        return dailyClicks;
    }

    public void setDailyClicks(List<AnalyticsPointResponse> dailyClicks) {
        this.dailyClicks = dailyClicks;
    }

    public List<AnalyticsPointResponse> getWeeklyClicks() {
        return weeklyClicks;
    }

    public void setWeeklyClicks(List<AnalyticsPointResponse> weeklyClicks) {
        this.weeklyClicks = weeklyClicks;
    }

    public List<AnalyticsPointResponse> getMonthlyClicks() {
        return monthlyClicks;
    }

    public void setMonthlyClicks(List<AnalyticsPointResponse> monthlyClicks) {
        this.monthlyClicks = monthlyClicks;
    }

    public List<AnalyticsMetricResponse> getTopCountries() {
        return topCountries;
    }

    public void setTopCountries(List<AnalyticsMetricResponse> topCountries) {
        this.topCountries = topCountries;
    }

    public List<AnalyticsMetricResponse> getTopBrowsers() {
        return topBrowsers;
    }

    public void setTopBrowsers(List<AnalyticsMetricResponse> topBrowsers) {
        this.topBrowsers = topBrowsers;
    }

    public List<AnalyticsMetricResponse> getTopDevices() {
        return topDevices;
    }

    public void setTopDevices(List<AnalyticsMetricResponse> topDevices) {
        this.topDevices = topDevices;
    }

    public List<AnalyticsMetricResponse> getTopUrls() {
        return topUrls;
    }

    public void setTopUrls(List<AnalyticsMetricResponse> topUrls) {
        this.topUrls = topUrls;
    }

    public List<AnalyticsMetricResponse> getMostActiveUsers() {
        return mostActiveUsers;
    }

    public void setMostActiveUsers(List<AnalyticsMetricResponse> mostActiveUsers) {
        this.mostActiveUsers = mostActiveUsers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AnalyticsDashboardResponse that = (AnalyticsDashboardResponse) o;
        return totalClicks == that.totalClicks
                && uniqueShortUrls == that.uniqueShortUrls
                && Objects.equals(dailyClicks, that.dailyClicks)
                && Objects.equals(weeklyClicks, that.weeklyClicks)
                && Objects.equals(monthlyClicks, that.monthlyClicks)
                && Objects.equals(topCountries, that.topCountries)
                && Objects.equals(topBrowsers, that.topBrowsers)
                && Objects.equals(topDevices, that.topDevices)
                && Objects.equals(topUrls, that.topUrls)
                && Objects.equals(mostActiveUsers, that.mostActiveUsers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(totalClicks, uniqueShortUrls, dailyClicks, weeklyClicks, monthlyClicks, topCountries, topBrowsers, topDevices, topUrls, mostActiveUsers);
    }

    @Override
    public String toString() {
        return "AnalyticsDashboardResponse{" +
                "totalClicks=" + totalClicks +
                ", uniqueShortUrls=" + uniqueShortUrls +
                ", dailyClicks=" + dailyClicks +
                ", weeklyClicks=" + weeklyClicks +
                ", monthlyClicks=" + monthlyClicks +
                ", topCountries=" + topCountries +
                ", topBrowsers=" + topBrowsers +
                ", topDevices=" + topDevices +
                ", topUrls=" + topUrls +
                ", mostActiveUsers=" + mostActiveUsers +
                '}';
    }
}
