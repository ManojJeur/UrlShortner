package com.project.urlshortener.controller;

import com.project.urlshortener.constants.ApiConstants;
import com.project.urlshortener.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping(ApiConstants.API_V1)
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, Object>>> health() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("service", "urlshortener");
        data.put("status", "UP");
        data.put("timestamp", Instant.now().toString());

        return ResponseEntity.ok(ApiResponse.success("Service is healthy", data));
    }
}
