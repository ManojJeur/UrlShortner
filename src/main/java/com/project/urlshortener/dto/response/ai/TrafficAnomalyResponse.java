package com.project.urlshortener.dto.response.ai;

import java.util.List;

public record TrafficAnomalyResponse(
        boolean unusual,
        String severity,
        String signal,
        String explanation,
        List<String> recommendedActions,
        String observedPeriod
) {
}
