package com.project.urlshortener.controller;

import com.project.urlshortener.constants.ApiConstants;
import com.project.urlshortener.dto.request.ai.AliasSuggestionRequest;
import com.project.urlshortener.dto.request.ai.AnalyticsQuestionRequest;
import com.project.urlshortener.dto.response.ai.AliasSuggestionResponse;
import com.project.urlshortener.dto.response.ai.AnalyticsQuestionResponse;
import com.project.urlshortener.dto.response.ai.InsightResponse;
import com.project.urlshortener.dto.response.ai.TrafficAnomalyResponse;
import com.project.urlshortener.response.ApiResponse;
import com.project.urlshortener.service.AiInsightsService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiConstants.API_V1 + "/ai")
public class AiController {

    private final AiInsightsService aiInsightsService;

    public AiController(AiInsightsService aiInsightsService) {
        this.aiInsightsService = aiInsightsService;
    }

    @PostMapping("/aliases/suggestions")
    public ResponseEntity<ApiResponse<AliasSuggestionResponse>> suggestAliases(@Valid @RequestBody AliasSuggestionRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Alias suggestions generated successfully", aiInsightsService.suggestAliases(request)));
    }

    @GetMapping("/traffic/summary")
    public ResponseEntity<ApiResponse<InsightResponse>> summarizeTraffic(@RequestParam(value = "days", defaultValue = "30") int days) {
        return ResponseEntity.ok(ApiResponse.success("Traffic summary generated successfully", aiInsightsService.summarizeTraffic(days)));
    }

    @GetMapping("/analytics/explain")
    public ResponseEntity<ApiResponse<InsightResponse>> explainAnalytics(@RequestParam(value = "days", defaultValue = "30") int days) {
        return ResponseEntity.ok(ApiResponse.success("Analytics explanation generated successfully", aiInsightsService.explainAnalytics(days)));
    }

    @GetMapping("/analytics/recommendations")
    public ResponseEntity<ApiResponse<InsightResponse>> recommendations(@RequestParam(value = "days", defaultValue = "30") int days) {
        return ResponseEntity.ok(ApiResponse.success("Recommendations generated successfully", aiInsightsService.generateRecommendations(days)));
    }

    @PostMapping("/analytics/questions")
    public ResponseEntity<ApiResponse<AnalyticsQuestionResponse>> answerQuestion(@Valid @RequestBody AnalyticsQuestionRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Analytics question answered successfully", aiInsightsService.answerQuestion(request)));
    }

    @GetMapping("/traffic/unusual")
    public ResponseEntity<ApiResponse<TrafficAnomalyResponse>> unusualTraffic(@RequestParam(value = "days", defaultValue = "14") int days) {
        return ResponseEntity.ok(ApiResponse.success("Traffic anomaly analysis generated successfully", aiInsightsService.detectUnusualTraffic(days)));
    }

    @GetMapping("/marketing/insights")
    public ResponseEntity<ApiResponse<InsightResponse>> marketingInsights(@RequestParam(value = "days", defaultValue = "30") int days) {
        return ResponseEntity.ok(ApiResponse.success("Marketing insights generated successfully", aiInsightsService.generateMarketingInsights(days)));
    }
}
