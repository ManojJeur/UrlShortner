package com.project.urlshortener.dto.request.ai;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AliasSuggestionRequest(
        @NotBlank(message = "Source URL is required")
        @Size(max = 2048, message = "Source URL must not exceed 2048 characters")
        String originalUrl,

        @Size(max = 120, message = "Page title must not exceed 120 characters")
        String pageTitle,

        @Size(max = 120, message = "Brand name must not exceed 120 characters")
        String brandName,

        @Size(max = 256, message = "Target keywords must not exceed 256 characters")
        String targetKeywords,

        @Size(max = 40, message = "Tone must not exceed 40 characters")
        String tone
) {
}
