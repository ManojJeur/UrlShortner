package com.project.urlshortener.service.impl;

import com.project.urlshortener.ai.AiGateway;
import com.project.urlshortener.ai.TrafficAnomalyDetector;
import com.project.urlshortener.ai.TrafficAnomalyObservation;
import com.project.urlshortener.constants.ErrorCodes;
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
import com.project.urlshortener.exception.ServiceUnavailableException;
import com.project.urlshortener.service.AiInsightsService;
import com.project.urlshortener.service.AnalyticsService;
import com.project.urlshortener.service.ReportingService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class AiInsightsServiceImpl implements AiInsightsService {

    private static final int DEFAULT_LIMIT = 5;
    private static final int MAX_DAYS = 365;

    private final ObjectProvider<AiGateway> aiGatewayProvider;
    private final AnalyticsService analyticsService;
    private final ReportingService reportingService;
    private final TrafficAnomalyDetector trafficAnomalyDetector;

    public AiInsightsServiceImpl(ObjectProvider<AiGateway> aiGatewayProvider,
                                 AnalyticsService analyticsService,
                                 ReportingService reportingService,
                                 TrafficAnomalyDetector trafficAnomalyDetector) {
        this.aiGatewayProvider = aiGatewayProvider;
        this.analyticsService = analyticsService;
        this.reportingService = reportingService;
        this.trafficAnomalyDetector = trafficAnomalyDetector;
    }

    @Override
    public AliasSuggestionResponse suggestAliases(AliasSuggestionRequest request) {
        AiGateway aiGateway = requireGateway();
        String systemPrompt = """
                You generate short, brand-safe, SEO-friendly URL aliases.
                Return lowercase aliases only.
                Use letters, numbers, and hyphens.
                Avoid punctuation, emojis, and spaces.
                Keep aliases under 32 characters.
                """;
        String userPrompt = buildAliasPrompt(request);
        AliasSuggestionResponse response = aiGateway.generateStructured(systemPrompt, userPrompt, AliasSuggestionResponse.class);
        return normalizeAliasResponse(request, response);
    }

    @Override
    public InsightResponse summarizeTraffic(int days) {
        return generateInsight(
                "Traffic Summary",
                days,
                """
                        Summarize the traffic in an executive-friendly way.
                        Focus on what happened, where the traffic came from, and what to do next.
                        """,
                """
                        Return a short traffic summary with 3 to 5 highlights and 3 practical recommendations.
                        """
        );
    }

    @Override
    public InsightResponse explainAnalytics(int days) {
        return generateInsight(
                "Analytics Explanation",
                days,
                """
                        Explain the analytics to a product or growth operator.
                        Translate the numbers into plain business language and identify notable patterns.
                        """,
                """
                        Explain the analytics data with 3 to 5 highlights and 3 to 5 recommendations.
                        """
        );
    }

    @Override
    public InsightResponse generateRecommendations(int days) {
        return generateInsight(
                "Recommendations",
                days,
                """
                        Generate prioritized product and growth recommendations based on the analytics.
                        Focus on actions that can improve traffic quality, click-through rate, and retention.
                        """,
                """
                        Return a concise recommendation set with business impact and implementation focus.
                        """
        );
    }

    @Override
    public AnalyticsQuestionResponse answerQuestion(AnalyticsQuestionRequest request) {
        AiGateway aiGateway = requireGateway();
        int safeDays = normalizeDays(request.days());
        String context = buildAnalyticsContext(safeDays, DEFAULT_LIMIT);
        String systemPrompt = """
                You answer questions about URL shortener analytics.
                Use only the supplied analytics context.
                If the data does not support a precise answer, say so clearly.
                """;
        String userPrompt = """
                Question: %s

                Analytics context:
                %s
                """.formatted(request.question(), context);
        AnalyticsQuestionResponse response = aiGateway.generateStructured(systemPrompt, userPrompt, AnalyticsQuestionResponse.class);
        return new AnalyticsQuestionResponse(
                request.question(),
                response.answer(),
                response.supportingSignals() == null ? List.of() : response.supportingSignals(),
                response.nextSteps() == null ? List.of() : response.nextSteps()
        );
    }

    @Override
    public TrafficAnomalyResponse detectUnusualTraffic(int days) {
        int safeDays = normalizeDays(days);
        List<AnalyticsPointResponse> dailyClicks = analyticsService.getDailyClicks(safeDays);
        TrafficAnomalyObservation observation = trafficAnomalyDetector.detect(dailyClicks);
        if (!observation.unusual()) {
            return new TrafficAnomalyResponse(
                    false,
                    observation.severity(),
                    observation.signal(),
                    observation.explanation(),
                    List.of("Continue monitoring the daily trend.", "Review the last seven days for campaign effects before taking action."),
                    safeDays + " days"
            );
        }

        AiGateway aiGateway = requireGateway();
        String systemPrompt = """
                You analyze traffic anomalies for a URL shortener.
                Explain the likely cause in business terms and recommend a response.
                Stay grounded in the supplied data.
                """;
        String userPrompt = """
                Anomaly signal:
                %s

                Observation:
                latestCount=%d
                baselineAverage=%.2f
                ratio=%.2f

                Daily clicks:
                %s
                """.formatted(
                observation.signal(),
                observation.latestCount(),
                observation.baselineAverage(),
                observation.ratio(),
                formatPoints(dailyClicks)
        );
        TrafficAnomalyResponse response = aiGateway.generateStructured(systemPrompt, userPrompt, TrafficAnomalyResponse.class);
        return new TrafficAnomalyResponse(
                true,
                severityOrFallback(response.severity(), observation.severity()),
                signalOrFallback(response.signal(), observation.signal()),
                explanationOrFallback(response.explanation(), observation.explanation()),
                normalizeStrings(response.recommendedActions()),
                safeDays + " days"
        );
    }

    @Override
    public InsightResponse generateMarketingInsights(int days) {
        return generateInsight(
                "Marketing Insights",
                days,
                """
                        Generate marketing and SEO insights from the latest traffic patterns.
                        Focus on acquisition opportunities, content themes, and audience signals.
                        """,
                """
                        Provide marketing insights with clear actions for content, SEO, and campaign planning.
                        """
        );
    }

    private InsightResponse generateInsight(String title, int days, String purpose, String instruction) {
        AiGateway aiGateway = requireGateway();
        int safeDays = normalizeDays(days);
        String context = buildAnalyticsContext(safeDays, DEFAULT_LIMIT);
        String systemPrompt = """
                You are a growth analyst for a URL shortener product.
                Use only the supplied business context.
                Return concise, actionable insights.
                """;
        String userPrompt = """
                Title: %s

                Purpose:
                %s

                Instruction:
                %s

                Context:
                %s
                """.formatted(title, purpose, instruction, context);
        InsightResponse response = aiGateway.generateStructured(systemPrompt, userPrompt, InsightResponse.class);
        return new InsightResponse(
                title,
                defaultString(response.summary()),
                normalizeStrings(response.highlights()),
                normalizeStrings(response.recommendations())
        );
    }

    private String buildAliasPrompt(AliasSuggestionRequest request) {
        String pageTitle = safeString(request.pageTitle());
        String brandName = safeString(request.brandName());
        String targetKeywords = safeString(request.targetKeywords());
        String tone = safeString(request.tone());
        return """
                Create alias suggestions for a short URL.

                Source URL: %s
                Brand name: %s
                Page title: %s
                Target keywords: %s
                Tone: %s

                Requirements:
                - Provide 1 primary alias and 6 alternates
                - Keep aliases short and memorable
                - Prefer SEO-friendly wording when possible
                - Return lowercase aliases with letters, numbers, and hyphens only
                - Avoid brand confusion and unsupported punctuation
                """.formatted(
                request.originalUrl(),
                pageTitle,
                brandName,
                targetKeywords,
                tone
        );
    }

    private AliasSuggestionResponse normalizeAliasResponse(AliasSuggestionRequest request, AliasSuggestionResponse response) {
        List<String> suggestions = normalizeAliases(response == null ? null : response.suggestions());
        String seoAlias = normalizeAliasCandidate(response == null ? null : response.seoFriendlyAlias());
        if (seoAlias == null) {
            seoAlias = suggestions.isEmpty() ? fallbackAlias(request.originalUrl(), request.pageTitle()) : suggestions.get(0);
        }
        if (suggestions.isEmpty()) {
            suggestions = defaultSuggestions(request.originalUrl(), request.pageTitle());
        }
        String rationale = defaultString(response == null ? null : response.rationale());
        String requestedAlias = suggestions.isEmpty() ? seoAlias : suggestions.get(0);
        List<String> keywords = normalizeStrings(response == null ? null : response.keywords());
        if (keywords.isEmpty() && request.targetKeywords() != null && !request.targetKeywords().isBlank()) {
            keywords = splitKeywords(request.targetKeywords());
        }
        return new AliasSuggestionResponse(requestedAlias, seoAlias, suggestions, keywords, rationale);
    }

    private String buildAnalyticsContext(int days, int limit) {
        AnalyticsDashboardResponse dashboard = analyticsService.getDashboard(days, limit);
        OperationsReportResponse report = reportingService.getOperationsReport();
        StringBuilder context = new StringBuilder();
        context.append("WindowDays: ").append(days).append('\n');
        context.append("GeneratedAtUTC: ").append(Instant.now().atZone(ZoneOffset.UTC)).append('\n');
        context.append("TotalUsers: ").append(report.getTotalUsers()).append('\n');
        context.append("TotalShortUrls: ").append(report.getTotalShortUrls()).append('\n');
        context.append("ActiveShortUrls: ").append(report.getActiveShortUrls()).append('\n');
        context.append("ExpiredShortUrls: ").append(report.getExpiredShortUrls()).append('\n');
        context.append("TotalClicks: ").append(report.getTotalClickEvents()).append('\n');
        context.append("TotalRefreshTokens: ").append(report.getTotalRefreshTokens()).append('\n');
        context.append("ActiveRefreshTokens: ").append(report.getActiveRefreshTokens()).append('\n');
        context.append("RevokedRefreshTokens: ").append(report.getRevokedRefreshTokens()).append('\n');
        context.append("ExpiredRefreshTokens: ").append(report.getExpiredRefreshTokens()).append('\n');
        context.append("DashboardTotalClicks: ").append(dashboard.getTotalClicks()).append('\n');
        context.append("DashboardUniqueShortUrls: ").append(dashboard.getUniqueShortUrls()).append('\n');
        context.append("DailyClicks: ").append(formatPoints(dashboard.getDailyClicks())).append('\n');
        context.append("WeeklyClicks: ").append(formatPoints(dashboard.getWeeklyClicks())).append('\n');
        context.append("MonthlyClicks: ").append(formatPoints(dashboard.getMonthlyClicks())).append('\n');
        context.append("TopCountries: ").append(formatMetrics(dashboard.getTopCountries())).append('\n');
        context.append("TopBrowsers: ").append(formatMetrics(dashboard.getTopBrowsers())).append('\n');
        context.append("TopDevices: ").append(formatMetrics(dashboard.getTopDevices())).append('\n');
        context.append("TopUrls: ").append(formatMetrics(dashboard.getTopUrls())).append('\n');
        context.append("MostActiveUsers: ").append(formatMetrics(dashboard.getMostActiveUsers()));
        return context.toString();
    }

    private AiGateway requireGateway() {
        AiGateway aiGateway = aiGatewayProvider.getIfAvailable();
        if (aiGateway == null) {
            throw new ServiceUnavailableException(
                    "AI features are disabled. Set APP_AI_ENABLED=true and configure an OpenAI API key to enable them.",
                    ErrorCodes.SERVICE_UNAVAILABLE
            );
        }
        return aiGateway;
    }

    private int normalizeDays(int days) {
        return Math.max(1, Math.min(days, MAX_DAYS));
    }

    private String formatPoints(List<AnalyticsPointResponse> points) {
        if (points == null || points.isEmpty()) {
            return "[]";
        }
        return points.stream()
                .filter(point -> point != null)
                .map(point -> point.getLabel() + "=" + point.getCount())
                .collect(Collectors.joining(", ", "[", "]"));
    }

    private String formatMetrics(List<AnalyticsMetricResponse> metrics) {
        if (metrics == null || metrics.isEmpty()) {
            return "[]";
        }
        return metrics.stream()
                .filter(metric -> metric != null)
                .map(metric -> metric.getName() + "=" + metric.getCount())
                .collect(Collectors.joining(", ", "[", "]"));
    }

    private List<String> normalizeAliases(List<String> aliases) {
        if (aliases == null || aliases.isEmpty()) {
            return new ArrayList<>();
        }
        return aliases.stream()
                .map(this::normalizeAliasCandidate)
                .filter(value -> value != null && !value.isBlank())
                .distinct()
                .limit(6)
                .collect(Collectors.toList());
    }

    private String normalizeAliasCandidate(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim().toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9\\-_]+", "-")
                .replaceAll("-{2,}", "-")
                .replaceAll("^-+", "")
                .replaceAll("-+$", "");
        if (normalized.isBlank()) {
            return null;
        }
        return normalized.length() > 32 ? normalized.substring(0, 32).replaceAll("-+$", "") : normalized;
    }

    private String fallbackAlias(String originalUrl, String pageTitle) {
        String source = safeString(pageTitle);
        if (source.isBlank()) {
            try {
                URI uri = new URI(originalUrl);
                source = uri.getHost() + " " + uri.getPath();
            } catch (URISyntaxException exception) {
                source = originalUrl;
            }
        }
        String normalized = source.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("-{2,}", "-")
                .replaceAll("^-+", "")
                .replaceAll("-+$", "");
        if (normalized.isBlank()) {
            normalized = "link";
        }
        return normalized.length() > 32 ? normalized.substring(0, 32).replaceAll("-+$", "") : normalized;
    }

    private List<String> defaultSuggestions(String originalUrl, String pageTitle) {
        String base = fallbackAlias(originalUrl, pageTitle);
        List<String> suggestions = new ArrayList<>();
        suggestions.add(base);
        suggestions.add(truncateAlias(base + "-guide"));
        suggestions.add(truncateAlias(base + "-tips"));
        suggestions.add(truncateAlias(base + "-read"));
        return suggestions.stream()
                .filter(value -> value != null && !value.isBlank())
                .distinct()
                .collect(Collectors.toList());
    }

    private List<String> splitKeywords(String targetKeywords) {
        if (targetKeywords == null || targetKeywords.isBlank()) {
            return List.of();
        }
        return List.of(targetKeywords.split(","))
                .stream()
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .limit(6)
                .collect(Collectors.toList());
    }

    private String truncateAlias(String value) {
        if (value == null) {
            return null;
        }
        String candidate = value.length() > 32 ? value.substring(0, 32) : value;
        return candidate.replaceAll("-+$", "");
    }

    private String defaultString(String value) {
        return value == null || value.isBlank() ? "" : value.trim();
    }

    private String safeString(String value) {
        return value == null ? "" : value.trim();
    }

    private List<String> normalizeStrings(List<String> values) {
        if (values == null || values.isEmpty()) {
            return List.of();
        }
        return values.stream()
                .map(this::safeString)
                .filter(value -> !value.isBlank())
                .distinct()
                .limit(6)
                .collect(Collectors.toList());
    }

    private String severityOrFallback(String responseSeverity, String fallback) {
        String value = defaultString(responseSeverity);
        return value.isBlank() ? fallback : value;
    }

    private String signalOrFallback(String responseSignal, String fallback) {
        String value = defaultString(responseSignal);
        return value.isBlank() ? fallback : value;
    }

    private String explanationOrFallback(String responseExplanation, String fallback) {
        String value = defaultString(responseExplanation);
        return value.isBlank() ? fallback : value;
    }
}
