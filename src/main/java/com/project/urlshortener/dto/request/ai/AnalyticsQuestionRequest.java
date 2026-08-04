package com.project.urlshortener.dto.request.ai;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record AnalyticsQuestionRequest(
        @NotBlank(message = "Question is required")
        String question,

        @Min(value = 1, message = "Days must be at least 1")
        @Max(value = 365, message = "Days must not exceed 365")
        int days
) {
}
