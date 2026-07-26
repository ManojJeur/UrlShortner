package com.project.urlshortener.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class RequestMetadataExtractor {

    public String resolveIpAddress(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    public String resolveCountry(HttpServletRequest request) {
        return firstHeaderValue(request, "CF-IPCountry", "X-Geo-Country", "X-Country");
    }

    public String resolveCity(HttpServletRequest request) {
        return firstHeaderValue(request, "X-Geo-City", "X-City");
    }

    public String resolveBrowser(HttpServletRequest request) {
        String userAgent = headerValue(request, "User-Agent");
        if (userAgent == null) {
            return "Unknown";
        }
        String normalized = userAgent.toLowerCase(Locale.ROOT);
        if (normalized.contains("edg/") || normalized.contains("edge/")) {
            return "Edge";
        }
        if (normalized.contains("opr/") || normalized.contains("opera")) {
            return "Opera";
        }
        if (normalized.contains("firefox/")) {
            return "Firefox";
        }
        if (normalized.contains("chrome/") && !normalized.contains("edg/") && !normalized.contains("opr/")) {
            return "Chrome";
        }
        if (normalized.contains("safari/") && !normalized.contains("chrome/")) {
            return "Safari";
        }
        return "Unknown";
    }

    public String resolveOperatingSystem(HttpServletRequest request) {
        String userAgent = headerValue(request, "User-Agent");
        if (userAgent == null) {
            return "Unknown";
        }
        String normalized = userAgent.toLowerCase(Locale.ROOT);
        if (normalized.contains("windows")) {
            return "Windows";
        }
        if (normalized.contains("mac os x") || normalized.contains("macintosh")) {
            return "Mac";
        }
        if (normalized.contains("android")) {
            return "Android";
        }
        if (normalized.contains("iphone") || normalized.contains("ipad") || normalized.contains("ios")) {
            return "iOS";
        }
        if (normalized.contains("linux")) {
            return "Linux";
        }
        return "Unknown";
    }

    public String resolveDevice(HttpServletRequest request) {
        String userAgent = headerValue(request, "User-Agent");
        if (userAgent == null) {
            return "Unknown";
        }
        String normalized = userAgent.toLowerCase(Locale.ROOT);
        if (normalized.contains("tablet")) {
            return "Tablet";
        }
        if (normalized.contains("mobile") || normalized.contains("iphone") || normalized.contains("android")) {
            return "Mobile";
        }
        return "Desktop";
    }

    public String resolveReferral(HttpServletRequest request) {
        String referral = headerValue(request, "Referer");
        return referral == null || referral.isBlank() ? "Direct" : referral;
    }

    private String firstHeaderValue(HttpServletRequest request, String... names) {
        for (String name : names) {
            String value = headerValue(request, name);
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return "Unknown";
    }

    private String headerValue(HttpServletRequest request, String name) {
        String value = request.getHeader(name);
        return value == null ? null : value.trim();
    }
}
