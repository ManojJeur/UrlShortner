package com.project.urlshortener.service;

import com.project.urlshortener.dto.request.CreateShortUrlRequest;
import com.project.urlshortener.dto.response.ShortUrlResponse;

public interface UrlService {

    ShortUrlResponse createShortUrl(String createdByEmail, CreateShortUrlRequest request);

    String resolveOriginalUrl(String shortCode);
}
