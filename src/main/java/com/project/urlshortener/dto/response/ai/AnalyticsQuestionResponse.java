package com.project.urlshortener.dto.response.ai;

import java.util.List;

public record AnalyticsQuestionResponse(
        String question,
        String answer,
        List<String> supportingSignals,
        List<String> nextSteps
) {
}
