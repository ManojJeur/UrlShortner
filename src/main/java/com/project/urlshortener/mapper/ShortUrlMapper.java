package com.project.urlshortener.mapper;

import com.project.urlshortener.config.ShortUrlProperties;
import com.project.urlshortener.dto.response.ShortUrlResponse;
import com.project.urlshortener.entity.ShortUrl;

public final class ShortUrlMapper {

    private ShortUrlMapper() {
    }

    public static ShortUrlResponse toResponse(ShortUrl shortUrl, ShortUrlProperties properties) {
        if (shortUrl == null) {
            return null;
        }
        String shortUrlValue = properties.getBaseUrl() + "/api/v1/r/" + shortUrl.getShortCode();
        return new ShortUrlResponse(
                shortUrl.getId(),
                shortUrl.getOriginalUrl(),
                shortUrl.getShortCode(),
                shortUrlValue,
                shortUrl.getCustomAlias(),
                shortUrl.getClickCount(),
                shortUrl.isEnabled(),
                shortUrl.getExpiresAt(),
                shortUrl.getCreatedAt()
        );
    }
}
