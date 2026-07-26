package com.project.urlshortener.service;

import com.project.urlshortener.dto.response.analytics.AnalyticsDashboardResponse;
import com.project.urlshortener.dto.response.analytics.AnalyticsMetricResponse;
import com.project.urlshortener.dto.response.analytics.AnalyticsPointResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface AnalyticsService {

    void recordClick(String shortCode, HttpServletRequest request);

    AnalyticsDashboardResponse getDashboard(int days, int topLimit);

    List<AnalyticsPointResponse> getDailyClicks(int days);

    List<AnalyticsPointResponse> getWeeklyClicks(int weeks);

    List<AnalyticsPointResponse> getMonthlyClicks(int months);

    List<AnalyticsMetricResponse> getTopCountries(int days, int limit);

    List<AnalyticsMetricResponse> getTopBrowsers(int days, int limit);

    List<AnalyticsMetricResponse> getTopDevices(int days, int limit);

    List<AnalyticsMetricResponse> getTopUrls(int days, int limit);

    List<AnalyticsMetricResponse> getMostActiveUsers(int days, int limit);
}
