package com.project.urlshortener.ai;

public record TrafficAnomalyObservation(
        boolean unusual,
        String severity,
        String signal,
        String explanation,
        long latestCount,
        double baselineAverage,
        double ratio
) {
}
