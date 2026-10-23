package com.project.urlshortener.controller;

import com.project.urlshortener.constants.ApiConstants;
import com.project.urlshortener.dto.request.CreateShortUrlRequest;
import com.project.urlshortener.dto.request.ShortUrlSearchRequest;
import com.project.urlshortener.dto.request.UpdateShortUrlRequest;
import com.project.urlshortener.dto.request.UpdateShortUrlStatusRequest;
import com.project.urlshortener.dto.response.PageResponse;
import com.project.urlshortener.dto.response.ShortUrlResponse;
import com.project.urlshortener.response.ApiResponse;
import com.project.urlshortener.service.AnalyticsService;
import com.project.urlshortener.service.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/urls")
    public ResponseEntity<ApiResponse<PageResponse<ShortUrlResponse>>> listMyShortUrls(
            Authentication authentication,
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "enabled", required = false) Boolean enabled,
            @RequestParam(value = "expired", required = false) Boolean expired,
            @RequestParam(value = "hasCustomAlias", required = false) Boolean hasCustomAlias,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "DESC") String sortDirection) {
        ShortUrlSearchRequest request = new ShortUrlSearchRequest(query, enabled, expired, hasCustomAlias, page, size, sortBy, sortDirection);
        PageResponse<ShortUrlResponse> response = urlService.listMyShortUrls(authentication.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Short URLs fetched successfully", response));
    }

    @PatchMapping("/urls/{shortUrlId}")
    public ResponseEntity<ApiResponse<ShortUrlResponse>> updateShortUrl(Authentication authentication,
                                                                        @PathVariable Long shortUrlId,
                                                                        @Valid @RequestBody UpdateShortUrlRequest request) {
        ShortUrlResponse response = urlService.updateShortUrl(authentication.getName(), shortUrlId, request);
        return ResponseEntity.ok(ApiResponse.success("Short URL updated successfully", response));
    }

    @PatchMapping("/urls/{shortUrlId}/status")
    public ResponseEntity<ApiResponse<ShortUrlResponse>> updateShortUrlStatus(Authentication authentication,
                                                                             @PathVariable Long shortUrlId,
                                                                             @Valid @RequestBody UpdateShortUrlStatusRequest request) {
        ShortUrlResponse response = urlService.updateShortUrlStatus(authentication.getName(), shortUrlId, request);
        return ResponseEntity.ok(ApiResponse.success("Short URL status updated successfully", response));
    }

    @DeleteMapping("/urls/{shortUrlId}")
    public ResponseEntity<ApiResponse<Void>> deleteShortUrl(Authentication authentication,
                                                            @PathVariable Long shortUrlId) {
        urlService.deleteShortUrl(authentication.getName(), shortUrlId);
        return ResponseEntity.ok(ApiResponse.success("Short URL deleted successfully"));
    }

    @GetMapping("/r/{shortCode}")
    @Operation(security = {})
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
