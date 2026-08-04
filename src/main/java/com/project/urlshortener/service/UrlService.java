package com.project.urlshortener.service;

import com.project.urlshortener.dto.request.CreateShortUrlRequest;
import com.project.urlshortener.dto.request.ShortUrlSearchRequest;
import com.project.urlshortener.dto.request.UpdateShortUrlRequest;
import com.project.urlshortener.dto.request.UpdateShortUrlStatusRequest;
import com.project.urlshortener.dto.response.PageResponse;
import com.project.urlshortener.dto.response.ShortUrlResponse;

public interface UrlService {

    ShortUrlResponse createShortUrl(String createdByEmail, CreateShortUrlRequest request);

    String resolveOriginalUrl(String shortCode);

    PageResponse<ShortUrlResponse> listMyShortUrls(String createdByEmail, ShortUrlSearchRequest request);

    ShortUrlResponse updateShortUrl(String createdByEmail, Long shortUrlId, UpdateShortUrlRequest request);

    ShortUrlResponse updateShortUrlStatus(String createdByEmail, Long shortUrlId, UpdateShortUrlStatusRequest request);

    void deleteShortUrl(String createdByEmail, Long shortUrlId);
}
