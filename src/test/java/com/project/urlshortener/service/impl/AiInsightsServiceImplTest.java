package com.project.urlshortener.service.impl;

import com.project.urlshortener.ai.AiGateway;
import com.project.urlshortener.ai.TrafficAnomalyDetector;
import com.project.urlshortener.ai.TrafficAnomalyObservation;
import com.project.urlshortener.dto.request.ai.AliasSuggestionRequest;
import com.project.urlshortener.dto.request.ai.AnalyticsQuestionRequest;
import com.project.urlshortener.dto.response.ai.AliasSuggestionResponse;
import com.project.urlshortener.dto.response.ai.AnalyticsQuestionResponse;
import com.project.urlshortener.dto.response.ai.InsightResponse;
import com.project.urlshortener.dto.response.ai.TrafficAnomalyResponse;
import com.project.urlshortener.dto.response.analytics.AnalyticsDashboardResponse;
import com.project.urlshortener.dto.response.analytics.AnalyticsMetricResponse;
import com.project.urlshortener.dto.response.analytics.AnalyticsPointResponse;
import com.project.urlshortener.dto.response.report.OperationsReportResponse;
import com.project.urlshortener.service.AnalyticsService;
import com.project.urlshortener.service.ReportingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiInsightsServiceImplTest {

    @Mock
    private ObjectProvider<AiGateway> aiGatewayProvider;

    @Mock
    private AiGateway aiGateway;

    @Mock
    private AnalyticsService analyticsService;

    @Mock
    private ReportingService reportingService;

    @Mock
    private TrafficAnomalyDetector trafficAnomalyDetector;

    private AiInsightsServiceImpl aiInsightsService;

    @BeforeEach
    void setUp() {
        aiInsightsService = new AiInsightsServiceImpl(aiGatewayProvider, analyticsService, reportingService, trafficAnomalyDetector);
    }

    @Test
    void suggestAliasesShouldNormalizeAiOutput() {
        when(aiGatewayProvider.getIfAvailable()).thenReturn(aiGateway);
        when(aiGateway.generateStructured(anyString(), anyString(), eq(AliasSuggestionResponse.class))).thenReturn(new AliasSuggestionResponse(
                "My Alias",
                "SEO Alias",
                List.of("My Alias", "Spring Boot Tips", "My Alias"),
                List.of("Spring Boot", "SEO"),
                "Reasoning"
        ));

        AliasSuggestionResponse response = aiInsightsService.suggestAliases(new AliasSuggestionRequest(
                "https://example.com/articles/spring-boot",
                "Spring Boot Tips",
                "Example",
                "spring boot, url shortener",
                "helpful"
        ));

        assertEquals("my-alias", response.requestedAlias());
        assertEquals("seo-alias", response.seoFriendlyAlias());
        assertTrue(response.suggestions().contains("spring-boot-tips"));
    }

    @Test
    void summarizeTrafficShouldUseGatewayAndAnalyticsContext() {
        when(aiGatewayProvider.getIfAvailable()).thenReturn(aiGateway);
        when(analyticsService.getDashboard(anyInt(), anyInt())).thenReturn(sampleDashboard());
        when(reportingService.getOperationsReport()).thenReturn(sampleReport());
        when(aiGateway.generateStructured(anyString(), anyString(), eq(InsightResponse.class))).thenReturn(new InsightResponse(
                "Traffic Summary",
                "Traffic is healthy.",
                List.of("Clicks grew steadily."),
                List.of("Keep publishing content.")
        ));

        InsightResponse response = aiInsightsService.summarizeTraffic(30);

        assertEquals("Traffic Summary", response.title());
        assertEquals("Traffic is healthy.", response.summary());
    }

    @Test
    void detectUnusualTrafficShouldSkipAiWhenTrafficIsNormal() {
        when(analyticsService.getDailyClicks(14)).thenReturn(List.of(
                point("2026-07-26", 10),
                point("2026-07-27", 11),
                point("2026-07-28", 9),
                point("2026-07-29", 10),
                point("2026-07-30", 12),
                point("2026-07-31", 11),
                point("2026-08-01", 10),
                point("2026-08-02", 12)
        ));
        when(trafficAnomalyDetector.detect(any())).thenReturn(new TrafficAnomalyObservation(
                false,
                "low",
                "Normal traffic",
                "No unusual spikes were detected.",
                12L,
                10.0d,
                1.2d
        ));
        when(aiGatewayProvider.getIfAvailable()).thenReturn(null);

        TrafficAnomalyResponse response = aiInsightsService.detectUnusualTraffic(14);

        assertFalse(response.unusual());
        verifyNoInteractions(aiGateway);
    }

    @Test
    void answerQuestionShouldReturnGatewayResponse() {
        when(aiGatewayProvider.getIfAvailable()).thenReturn(aiGateway);
        when(analyticsService.getDashboard(anyInt(), anyInt())).thenReturn(sampleDashboard());
        when(reportingService.getOperationsReport()).thenReturn(sampleReport());
        when(aiGateway.generateStructured(anyString(), anyString(), eq(AnalyticsQuestionResponse.class))).thenReturn(new AnalyticsQuestionResponse(
                "What caused the growth?",
                "The growth came from repeated clicks on the top link.",
                List.of("Top URL led the dashboard."),
                List.of("Promote the best-performing page.")
        ));

        AnalyticsQuestionResponse response = aiInsightsService.answerQuestion(new AnalyticsQuestionRequest("What caused the growth?", 30));

        assertEquals("What caused the growth?", response.question());
        assertEquals("The growth came from repeated clicks on the top link.", response.answer());
    }

    private AnalyticsDashboardResponse sampleDashboard() {
        return new AnalyticsDashboardResponse(
                42L,
                7L,
                List.of(point("2026-08-01", 12)),
                List.of(point("2026-W31", 42)),
                List.of(point("2026-08", 42)),
                List.of(metric("India", 20)),
                List.of(metric("Chrome", 18)),
                List.of(metric("Desktop", 30)),
                List.of(metric("springtips", 15)),
                List.of(metric("user@example.com", 12))
        );
    }

    private OperationsReportResponse sampleReport() {
        return new OperationsReportResponse(
                Instant.parse("2026-08-02T00:00:00Z"),
                10L,
                20L,
                18L,
                2L,
                42L,
                6L,
                5L,
                1L,
                0L
        );
    }

    private AnalyticsPointResponse point(String label, long count) {
        return new AnalyticsPointResponse(label, count);
    }

    private AnalyticsMetricResponse metric(String name, long count) {
        return new AnalyticsMetricResponse(name, count);
    }
}
