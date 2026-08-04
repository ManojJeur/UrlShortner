package com.project.urlshortener.dto.response.ai;

import java.util.List;

public record InsightResponse(
        String title,
        String summary,
        List<String> highlights,
        List<String> recommendations
) {
}
