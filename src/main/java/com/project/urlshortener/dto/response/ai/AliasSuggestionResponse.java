package com.project.urlshortener.dto.response.ai;

import java.util.List;

public record AliasSuggestionResponse(
        String requestedAlias,
        String seoFriendlyAlias,
        List<String> suggestions,
        List<String> keywords,
        String rationale
) {
}
