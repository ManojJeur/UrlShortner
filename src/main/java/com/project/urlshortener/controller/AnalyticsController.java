package com.project.urlshortener.controller;

import com.project.urlshortener.constants.ApiConstants;
import com.project.urlshortener.dto.response.analytics.AnalyticsDashboardResponse;
import com.project.urlshortener.dto.response.analytics.AnalyticsMetricResponse;
import com.project.urlshortener.dto.response.analytics.AnalyticsPointResponse;
import com.project.urlshortener.response.ApiResponse;
import com.project.urlshortener.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(ApiConstants.API_V1 + "/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<AnalyticsDashboardResponse>> dashboard(
            @RequestParam(value = "days", defaultValue = "30") int days,
            @RequestParam(value = "limit", defaultValue = "5") int limit) {
        AnalyticsDashboardResponse response = analyticsService.getDashboard(days, limit);
        return ResponseEntity.ok(ApiResponse.success("Analytics dashboard fetched successfully", response));
    }

    @GetMapping("/daily")
    public ResponseEntity<ApiResponse<List<AnalyticsPointResponse>>> daily(
            @RequestParam(value = "days", defaultValue = "30") int days) {
        return ResponseEntity.ok(ApiResponse.success("Daily clicks fetched successfully", analyticsService.getDailyClicks(days)));
    }

    @GetMapping("/weekly")
    public ResponseEntity<ApiResponse<List<AnalyticsPointResponse>>> weekly(
            @RequestParam(value = "weeks", defaultValue = "12") int weeks) {
        return ResponseEntity.ok(ApiResponse.success("Weekly clicks fetched successfully", analyticsService.getWeeklyClicks(weeks)));
    }

    @GetMapping("/monthly")
    public ResponseEntity<ApiResponse<List<AnalyticsPointResponse>>> monthly(
            @RequestParam(value = "months", defaultValue = "12") int months) {
        return ResponseEntity.ok(ApiResponse.success("Monthly clicks fetched successfully", analyticsService.getMonthlyClicks(months)));
    }

    @GetMapping("/top-countries")
    public ResponseEntity<ApiResponse<List<AnalyticsMetricResponse>>> topCountries(
            @RequestParam(value = "days", defaultValue = "30") int days,
            @RequestParam(value = "limit", defaultValue = "5") int limit) {
        return ResponseEntity.ok(ApiResponse.success("Top countries fetched successfully", analyticsService.getTopCountries(days, limit)));
    }

    @GetMapping("/top-browsers")
    public ResponseEntity<ApiResponse<List<AnalyticsMetricResponse>>> topBrowsers(
            @RequestParam(value = "days", defaultValue = "30") int days,
            @RequestParam(value = "limit", defaultValue = "5") int limit) {
        return ResponseEntity.ok(ApiResponse.success("Top browsers fetched successfully", analyticsService.getTopBrowsers(days, limit)));
    }

    @GetMapping("/top-devices")
    public ResponseEntity<ApiResponse<List<AnalyticsMetricResponse>>> topDevices(
            @RequestParam(value = "days", defaultValue = "30") int days,
            @RequestParam(value = "limit", defaultValue = "5") int limit) {
        return ResponseEntity.ok(ApiResponse.success("Top devices fetched successfully", analyticsService.getTopDevices(days, limit)));
    }

    @GetMapping("/top-urls")
    public ResponseEntity<ApiResponse<List<AnalyticsMetricResponse>>> topUrls(
            @RequestParam(value = "days", defaultValue = "30") int days,
            @RequestParam(value = "limit", defaultValue = "5") int limit) {
        return ResponseEntity.ok(ApiResponse.success("Top URLs fetched successfully", analyticsService.getTopUrls(days, limit)));
    }

    @GetMapping("/top-users")
    public ResponseEntity<ApiResponse<List<AnalyticsMetricResponse>>> topUsers(
            @RequestParam(value = "days", defaultValue = "30") int days,
            @RequestParam(value = "limit", defaultValue = "5") int limit) {
        return ResponseEntity.ok(ApiResponse.success("Most active users fetched successfully", analyticsService.getMostActiveUsers(days, limit)));
    }
}
