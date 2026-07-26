package com.project.urlshortener.service.impl;

import com.project.urlshortener.cache.ShortUrlCacheEntry;
import com.project.urlshortener.cache.ShortUrlCacheService;
import com.project.urlshortener.constants.ErrorCodes;
import com.project.urlshortener.dto.response.analytics.AnalyticsDashboardResponse;
import com.project.urlshortener.dto.response.analytics.AnalyticsMetricResponse;
import com.project.urlshortener.dto.response.analytics.AnalyticsPointResponse;
import com.project.urlshortener.entity.ClickEvent;
import com.project.urlshortener.entity.ShortUrl;
import com.project.urlshortener.entity.User;
import com.project.urlshortener.exception.ResourceNotFoundException;
import com.project.urlshortener.repository.ClickEventRepository;
import com.project.urlshortener.repository.ShortUrlRepository;
import com.project.urlshortener.service.AnalyticsService;
import com.project.urlshortener.util.RequestMetadataExtractor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    private static final DateTimeFormatter DAILY_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter MONTHLY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    private final ClickEventRepository clickEventRepository;
    private final ShortUrlRepository shortUrlRepository;
    private final RequestMetadataExtractor requestMetadataExtractor;
    private final ShortUrlCacheService shortUrlCacheService;

    public AnalyticsServiceImpl(ClickEventRepository clickEventRepository,
                                ShortUrlRepository shortUrlRepository,
                                RequestMetadataExtractor requestMetadataExtractor,
                                ShortUrlCacheService shortUrlCacheService) {
        this.clickEventRepository = clickEventRepository;
        this.shortUrlRepository = shortUrlRepository;
        this.requestMetadataExtractor = requestMetadataExtractor;
        this.shortUrlCacheService = shortUrlCacheService;
    }

    @Override
    @Transactional
    public void recordClick(String shortCode, HttpServletRequest request) {
        ShortUrl shortUrl = shortUrlRepository.findByShortCode(normalize(shortCode))
                .orElseThrow(() -> new ResourceNotFoundException("Short URL not found", ErrorCodes.RESOURCE_NOT_FOUND));

        ShortUrlCacheEntry cacheEntry = shortUrlCacheService.get(shortUrl.getShortCode()).orElse(null);
        if (cacheEntry != null && !cacheEntry.isEnabled()) {
            throw new ResourceNotFoundException("Short URL is disabled", ErrorCodes.RESOURCE_NOT_FOUND);
        }

        ClickEvent clickEvent = new ClickEvent();
        clickEvent.setShortUrl(shortUrl);
        clickEvent.setCreatedBy(shortUrl.getCreatedBy());
        clickEvent.setIpAddress(requestMetadataExtractor.resolveIpAddress(request));
        clickEvent.setCountry(requestMetadataExtractor.resolveCountry(request));
        clickEvent.setCity(requestMetadataExtractor.resolveCity(request));
        clickEvent.setBrowser(requestMetadataExtractor.resolveBrowser(request));
        clickEvent.setOperatingSystem(requestMetadataExtractor.resolveOperatingSystem(request));
        clickEvent.setDevice(requestMetadataExtractor.resolveDevice(request));
        clickEvent.setReferral(requestMetadataExtractor.resolveReferral(request));
        clickEvent.setClickedAt(Instant.now());
        clickEventRepository.save(clickEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public AnalyticsDashboardResponse getDashboard(int days, int topLimit) {
        List<ClickEvent> events = loadEvents(days);
        return new AnalyticsDashboardResponse(
                events.size(),
                events.stream().map(event -> event.getShortUrl().getShortCode()).distinct().count(),
                buildDailyClicks(events, days),
                buildWeeklyClicks(events, Math.max(1, Math.min(days / 7 + 1, 12))),
                buildMonthlyClicks(events, Math.max(1, Math.min(days / 30 + 1, 12))),
                buildTopCountries(events, topLimit),
                buildTopBrowsers(events, topLimit),
                buildTopDevices(events, topLimit),
                buildTopUrls(events, topLimit),
                buildMostActiveUsers(events, topLimit)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnalyticsPointResponse> getDailyClicks(int days) {
        List<ClickEvent> events = loadEvents(days);
        return buildDailyClicks(events, days);
    }

    private List<AnalyticsPointResponse> buildDailyClicks(List<ClickEvent> events, int days) {
        LocalDate end = LocalDate.now(ZoneOffset.UTC);
        LocalDate start = end.minusDays(Math.max(0, days - 1L));
        return buildDateSeries(events, start, end, event -> event.getClickedAt().atZone(ZoneOffset.UTC).toLocalDate(),
                date -> DAILY_FORMATTER.format(date), LocalDate::plusDays);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnalyticsPointResponse> getWeeklyClicks(int weeks) {
        int safeWeeks = Math.max(1, weeks);
        List<ClickEvent> events = loadEvents(safeWeeks * 7);
        return buildWeeklyClicks(events, safeWeeks);
    }

    private List<AnalyticsPointResponse> buildWeeklyClicks(List<ClickEvent> events, int weeks) {
        int safeWeeks = Math.max(1, weeks);
        LocalDate end = LocalDate.now(ZoneOffset.UTC);
        LocalDate start = end.minusWeeks(safeWeeks - 1L);
        WeekFields weekFields = WeekFields.ISO;
        return buildDateSeries(events, start, end, event -> event.getClickedAt().atZone(ZoneOffset.UTC).toLocalDate(),
                date -> String.format(Locale.ROOT, "%d-W%02d", date.get(weekFields.weekBasedYear()), date.get(weekFields.weekOfWeekBasedYear())),
                LocalDate::plusWeeks);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnalyticsPointResponse> getMonthlyClicks(int months) {
        int safeMonths = Math.max(1, months);
        List<ClickEvent> events = loadEvents(safeMonths * 31);
        return buildMonthlyClicks(events, safeMonths);
    }

    private List<AnalyticsPointResponse> buildMonthlyClicks(List<ClickEvent> events, int months) {
        int safeMonths = Math.max(1, months);
        YearMonth end = YearMonth.now(ZoneOffset.UTC);
        YearMonth start = end.minusMonths(safeMonths - 1L);
        return buildMonthSeries(events, start, end);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnalyticsMetricResponse> getTopCountries(int days, int limit) {
        return buildTopCountries(loadEvents(days), limit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnalyticsMetricResponse> getTopBrowsers(int days, int limit) {
        return buildTopBrowsers(loadEvents(days), limit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnalyticsMetricResponse> getTopDevices(int days, int limit) {
        return buildTopDevices(loadEvents(days), limit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnalyticsMetricResponse> getTopUrls(int days, int limit) {
        return buildTopUrls(loadEvents(days), limit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnalyticsMetricResponse> getMostActiveUsers(int days, int limit) {
        return buildMostActiveUsers(loadEvents(days), limit);
    }

    private List<AnalyticsMetricResponse> buildTopCountries(List<ClickEvent> events, int limit) {
        return topMetrics(events, event -> normalizeMetric(event.getCountry()), limit);
    }

    private List<AnalyticsMetricResponse> buildTopBrowsers(List<ClickEvent> events, int limit) {
        return topMetrics(events, event -> normalizeMetric(event.getBrowser()), limit);
    }

    private List<AnalyticsMetricResponse> buildTopDevices(List<ClickEvent> events, int limit) {
        return topMetrics(events, event -> normalizeMetric(event.getDevice()), limit);
    }

    private List<AnalyticsMetricResponse> buildTopUrls(List<ClickEvent> events, int limit) {
        return topMetrics(events, event -> event.getShortUrl().getShortCode() + " | " + event.getShortUrl().getOriginalUrl(), limit);
    }

    private List<AnalyticsMetricResponse> buildMostActiveUsers(List<ClickEvent> events, int limit) {
        return topMetrics(events, event -> event.getCreatedBy().getEmail(), limit);
    }

    private List<ClickEvent> loadEvents(int days) {
        int safeDays = Math.max(1, days);
        Instant end = Instant.now();
        Instant start = end.minusSeconds((long) safeDays * 24L * 60L * 60L);
        return clickEventRepository.findByClickedAtBetweenOrderByClickedAtAsc(start, end);
    }

    private List<AnalyticsPointResponse> buildDateSeries(List<ClickEvent> events,
                                                         LocalDate start,
                                                         LocalDate end,
                                                         Function<ClickEvent, LocalDate> bucketExtractor,
                                                         Function<LocalDate, String> labelFormatter,
                                                         java.util.function.UnaryOperator<LocalDate> increment) {
        Map<String, Long> counts = events.stream()
                .collect(Collectors.groupingBy(event -> labelFormatter.apply(bucketExtractor.apply(event)), LinkedHashMap::new, Collectors.counting()));

        List<AnalyticsPointResponse> points = new ArrayList<>();
        LocalDate cursor = start;
        while (!cursor.isAfter(end)) {
            String label = labelFormatter.apply(cursor);
            points.add(new AnalyticsPointResponse(label, counts.getOrDefault(label, 0L)));
            cursor = increment.apply(cursor);
        }
        return points;
    }

    private List<AnalyticsPointResponse> buildMonthSeries(List<ClickEvent> events, YearMonth start, YearMonth end) {
        Map<String, Long> counts = events.stream()
                .collect(Collectors.groupingBy(event -> MONTHLY_FORMATTER.format(event.getClickedAt().atZone(ZoneOffset.UTC)), LinkedHashMap::new, Collectors.counting()));

        List<AnalyticsPointResponse> points = new ArrayList<>();
        YearMonth cursor = start;
        while (!cursor.isAfter(end)) {
            String label = MONTHLY_FORMATTER.format(cursor.atDay(1));
            points.add(new AnalyticsPointResponse(label, counts.getOrDefault(label, 0L)));
            cursor = cursor.plusMonths(1);
        }
        return points;
    }

    private List<AnalyticsMetricResponse> topMetrics(List<ClickEvent> events, Function<ClickEvent, String> extractor, int limit) {
        int safeLimit = Math.max(1, limit);
        return events.stream()
                .map(extractor)
                .filter(Objects::nonNull)
                .filter(value -> !value.isBlank())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder()))
                .limit(safeLimit)
                .map(entry -> new AnalyticsMetricResponse(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private String normalizeMetric(String value) {
        return value == null || value.isBlank() ? "Unknown" : value.trim();
    }

    private String normalize(String value) {
        return value == null ? null : value.trim().toLowerCase(Locale.ROOT);
    }
}
