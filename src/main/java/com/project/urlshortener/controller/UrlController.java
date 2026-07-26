package com.project.urlshortener.controller;

import com.project.urlshortener.constants.ApiConstants;
import com.project.urlshortener.dto.request.CreateShortUrlRequest;
import com.project.urlshortener.dto.response.ShortUrlResponse;
import com.project.urlshortener.response.ApiResponse;
import com.project.urlshortener.service.AnalyticsService;
import com.project.urlshortener.service.UrlService;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping(ApiConstants.API_V1)
public class UrlController {

    private static final Logger log = LoggerFactory.getLogger(UrlController.class);

    private final UrlService urlService;
    private final AnalyticsService analyticsService;

    public UrlController(UrlService urlService, AnalyticsService analyticsService) {
        this.urlService = urlService;
        this.analyticsService = analyticsService;
    }

    @PostMapping("/urls")
    public ResponseEntity<ApiResponse<ShortUrlResponse>> createShortUrl(Authentication authentication,
                                                                        @Valid @RequestBody CreateShortUrlRequest request) {
        ShortUrlResponse response = urlService.createShortUrl(authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Short URL created successfully", response));
    }

    @GetMapping("/r/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode, HttpServletRequest request) {
        String originalUrl = urlService.resolveOriginalUrl(shortCode);
        try {
            analyticsService.recordClick(shortCode, request);
        } catch (Exception exception) {
            log.warn("Failed to record analytics for short code {}", shortCode, exception);
        }
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, URI.create(originalUrl).toString())
                .build();
    }
}
