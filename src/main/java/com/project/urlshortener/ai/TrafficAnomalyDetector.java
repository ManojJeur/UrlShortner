package com.project.urlshortener.ai;

import com.project.urlshortener.dto.response.analytics.AnalyticsPointResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class TrafficAnomalyDetector {

    public TrafficAnomalyObservation detect(List<AnalyticsPointResponse> dailyClicks) {
        if (dailyClicks == null || dailyClicks.size() < 8) {
            return new TrafficAnomalyObservation(false, "low", "Not enough history to evaluate traffic", "Need at least 8 daily points to compare recent activity against a baseline.", 0L, 0.0d, 0.0d);
        }

        List<Long> counts = new ArrayList<>();
        for (AnalyticsPointResponse point : dailyClicks) {
            counts.add(point == null ? 0L : Math.max(0L, point.getCount()));
        }

        long latest = counts.get(counts.size() - 1);
        List<Long> baselineWindow = counts.subList(Math.max(0, counts.size() - 8), counts.size() - 1);
        double baselineAverage = baselineWindow.stream().mapToLong(Long::longValue).average().orElse(0.0d);
        double ratio = baselineAverage <= 0.0d ? (latest > 0 ? Double.POSITIVE_INFINITY : 1.0d) : latest / baselineAverage;
        double variance = baselineWindow.stream()
                .mapToDouble(value -> Math.pow(value - baselineAverage, 2))
                .average()
                .orElse(0.0d);
        double standardDeviation = Math.sqrt(variance);
        double zScore = standardDeviation <= 0.0d ? (latest > baselineAverage ? Double.POSITIVE_INFINITY : 0.0d) : (latest - baselineAverage) / standardDeviation;

        boolean unusual = latest >= 2L && (ratio >= 1.75d || zScore >= 2.5d);
        String severity = buildSeverity(ratio, zScore, unusual);
        String signal = unusual
                ? "Recent daily clicks rose sharply compared with the trailing baseline."
                : "Daily clicks are within normal variance for the observed window.";
        String explanation = unusual
                ? "The latest day is materially above the trailing 7-day baseline and should be reviewed for campaign, referral, or bot-driven activity."
                : "There is no strong spike or drop in the last day relative to the preceding week.";

        return new TrafficAnomalyObservation(unusual, severity, signal, explanation, latest, baselineAverage, ratio);
    }

    private String buildSeverity(double ratio, double zScore, boolean unusual) {
        if (!unusual) {
            return "low";
        }
        if (ratio >= 3.0d || zScore >= 4.0d) {
            return "high";
        }
        if (ratio >= 2.0d || zScore >= 3.0d) {
            return "medium";
        }
        return "elevated";
    }
}
