package com.project.urlshortener.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.urlshortener.cache.RedisRateLimiter;
import com.project.urlshortener.config.RedisAppProperties;
import com.project.urlshortener.response.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final RedisRateLimiter redisRateLimiter;
    private final RedisAppProperties redisAppProperties;
    private final ObjectMapper objectMapper;

    public RateLimitFilter(RedisRateLimiter redisRateLimiter,
                           RedisAppProperties redisAppProperties,
                           ObjectMapper objectMapper) {
        this.redisRateLimiter = redisRateLimiter;
        this.redisAppProperties = redisAppProperties;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        RateLimitRule rule = resolveRule(request.getRequestURI());
        if (rule == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String identifier = resolveIdentifier(request);
        boolean allowed = redisRateLimiter.tryAcquire(rule.keyPrefix, identifier, rule.limit, Duration.ofSeconds(rule.windowSeconds));
        if (!allowed) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(response.getWriter(),
                    ErrorResponse.of("Too many requests", "RATE_LIMIT_EXCEEDED"));
            return;
        }

        filterChain.doFilter(request, response);
    }

    private RateLimitRule resolveRule(String uri) {
        if (uri.startsWith("/api/v1/auth/")) {
            return new RateLimitRule("rate_limit:auth", redisAppProperties.getAuthRateLimit(), redisAppProperties.getAuthRateLimitWindowSeconds());
        }
        if ("/api/v1/urls".equals(uri)) {
            return new RateLimitRule("rate_limit:url_create", redisAppProperties.getUrlCreateRateLimit(), redisAppProperties.getUrlCreateRateLimitWindowSeconds());
        }
        if (uri.startsWith("/api/v1/r/")) {
            return new RateLimitRule("rate_limit:redirect", redisAppProperties.getRedirectRateLimit(), redisAppProperties.getRedirectRateLimitWindowSeconds());
        }
        return null;
    }

    private String resolveIdentifier(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)
                && authentication.getName() != null) {
            return authentication.getName();
        }

        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }

    private static class RateLimitRule {
        private final String keyPrefix;
        private final int limit;
        private final int windowSeconds;

        private RateLimitRule(String keyPrefix, int limit, int windowSeconds) {
            this.keyPrefix = keyPrefix;
            this.limit = limit;
            this.windowSeconds = windowSeconds;
        }
    }
}
